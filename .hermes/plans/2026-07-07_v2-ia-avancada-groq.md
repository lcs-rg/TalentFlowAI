# V2 — Inteligência Artificial Avançada (Plano de Implementação)

> **Para Hermes:** Use development-workflow. Implementar task por task, TDD onde aplicável.

**Objetivo:** Transformar a IA de um copiloto básico (V1) em um diferencial competitivo com busca semântica, embeddings, recomendações inteligentes e dashboard IA.

**Stack:** GROQ (chat/LLM) + sentence-transformers local (embeddings) + Supabase pgvector

**Provider:** GROQ — API OpenAI-compatible. Substitui totalmente OpenAI. Embeddings via modelo local no AI Service Python.

---

## Arquitetura V2

```
Frontend (Next.js)
    ↓ REST
Backend (Spring Boot) — proxy todos os endpoints de IA
    ↓ REST
AI Service (Python/FastAPI)
    ├── GROQ API (chat: mixtral-8x7b-32768 / llama-3.3-70b)
    ├── sentence-transformers (embeddings: all-MiniLM-L6-v2, 384d)
    └── Supabase pgvector (armazenamento + busca vetorial)
```

---

### Task 1: Migrar AI Service para GROQ + embeddings local

**Objetivo:** Substituir OpenAI por GROQ para chat e adicionar sentence-transformers para embeddings.

**Arquivos:**
- Modificar: `ai-service/main.py`
- Modificar: `ai-service/requirements.txt`
- Modificar: `ai-service/prompts/job-candidate-match.txt`
- Criar: `ai-service/prompts/resume-parser-v2.txt`
- Criar: `ai-service/prompts/job-recommendation.txt`
- Criar: `ai-service/prompts/candidate-recommendation.txt`

**Passos:**
1. Atualizar `requirements.txt`: substituir `openai` por `groq`, adicionar `sentence-transformers`
2. Alterar cliente de `OpenAI` para `Groq` (API compatível, só muda base_url + api_key)
3. Adicionar endpoint `/embed` que gera embeddings via sentence-transformers
4. Atualizar endpoints existentes para usar GROQ
5. Testar com `curl` local

---

### Task 2: Configurar GROQ no backend

**Objetivo:** Adicionar configuração GROQ ao Spring Boot.

**Arquivos:**
- Modificar: `backend/src/main/resources/application.yml`
- Modificar: `backend/src/main/java/com/talentflow/infrastructure/ai/AIServiceClient.java`

**Passos:**
1. Adicionar `app.groq.api-key` e `app.groq.chat-model` no application.yml
2. Atualizar AIServiceClient se necessário (a URL do AI Service não muda)

---

### Task 3: Endpoint de embedding e busca semântica

**Objetivo:** Gerar embeddings de vagas e candidatos para busca vetorial.

**Arquivos:**
- Criar: `backend/src/main/java/com/talentflow/domain/ai/EmbeddingService.java`
- Modificar: `ai-service/main.py` (já feito na Task 1)
- Modificar: `backend/src/main/java/com/talentflow/infrastructure/ai/AIServiceClient.java`

**Endpoints novos no AI Service:**
- `POST /embed` — recebe texto, retorna `float[]` (384 dimensões)

**Endpoints novos no backend:**
- `POST /api/v1/ai/embed-job/{jobId}` — gera e armazena embedding da vaga
- `POST /api/v1/ai/embed-candidate/{candidateId}` — gera e armazena embedding do candidato

**Lógica:**
1. Backend chama AI Service `/embed` com texto da vaga/candidato
2. AI Service usa sentence-transformers para gerar vetor 384d
3. Backend armazena na tabela `job_embeddings` ou `candidate_embeddings` via pgvector

---

### Task 4: Migration — tabela job_embeddings

**Objetivo:** Criar tabela para embeddings de vagas (candidate_embeddings já existe na V16).

**Arquivos:**
- Criar: `backend/src/main/resources/db/migration/V21__create_job_embeddings.sql`

```sql
CREATE TABLE IF NOT EXISTS job_embeddings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    embedding vector(384),
    model_version VARCHAR(50) NOT NULL DEFAULT 'all-MiniLM-L6-v2',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(job_id)
);
CREATE INDEX IF NOT EXISTS idx_job_embeddings_vector ON job_embeddings USING ivfflat (embedding vector_cosine_ops);
```

---

### Task 5: Busca semântica de candidatos para uma vaga

**Objetivo:** Dado um jobId, encontrar candidatos similares via similaridade de cosseno no pgvector.

**Arquivos:**
- Criar: `backend/src/main/java/com/talentflow/application/ai/SemanticSearchService.java`
- Modificar: `backend/src/main/java/com/talentflow/infrastructure/persistence/AllRepositories.java` (adicionar CandidateEmbeddingJpaEntity + repo se não existir)
- Criar: `backend/src/main/java/com/talentflow/presentation/api/v1/SemanticSearchController.java`

**Endpoint:** `GET /api/v1/ai/search-candidates/{jobId}?topK=10`

**Lógica:**
1. Buscar embedding da vaga em `job_embeddings`
2. Se não existir, gerar via AI Service e armazenar
3. Query pgvector: `SELECT candidate_id, 1 - (embedding <=> $1) AS similarity FROM candidate_embeddings ORDER BY similarity DESC LIMIT $2`
4. Retornar candidatos ranqueados por similaridade

---

### Task 6: Recomendações de vagas para candidato

**Objetivo:** Dado um candidateId, recomendar vagas compatíveis usando GROQ + embeddings.

**Arquivos:**
- Criar: `backend/src/main/java/com/talentflow/application/ai/RecommendationService.java`
- Modificar: `backend/src/main/java/com/talentflow/presentation/api/v1/AIController.java`

**Endpoint:** `GET /api/v1/ai/recommend-jobs/{candidateId}?topK=5`

**Lógica (híbrida):**
1. Busca vetorial: top-K vagas por similaridade de embedding
2. Refinamento GROQ: enviar top-K + perfil do candidato para o modelo ranquear com justificativa
3. Retornar lista com score + explicação por vaga

---

### Task 7: Recomendações de candidatos para vaga

**Objetivo:** Dado um jobId, recomendar melhores candidatos.

**Endpoint:** `GET /api/v1/ai/recommend-candidates/{jobId}?topK=10`

**Lógica:** Similar à Task 6, invertendo vaga ↔ candidato.

---

### Task 8: Dashboard IA (métricas inteligentes)

**Objetivo:** Enriquece o dashboard existente com métricas de IA.

**Arquivos:**
- Modificar: `backend/src/main/java/com/talentflow/application/recruitment/JobService.java` (método getDashboard)
- Modificar: `backend/src/main/java/com/talentflow/presentation/dto/response/DashboardResponse.java`

**Novas métricas:**
- `avgMatchScore` — score médio de compatibilidade dos candidatos
- `topCandidates` — top 3 candidatos com maior score
- `pipelineHealth` — % de candidatos por estágio
- `timeToHire` — dias médios até contratação

---

### Task 9: Filtros inteligentes na listagem de candidatos

**Objetivo:** Ordenar/filtrar candidatos por score de compatibilidade com uma vaga.

**Arquivos:**
- Modificar: `backend/src/main/java/com/talentflow/presentation/api/v1/CandidateController.java`
- Modificar: `backend/src/main/java/com/talentflow/application/recruitment/CandidateService.java`

**Endpoint:** `GET /api/v1/candidates?jobId={jobId}&sortBy=matchScore`

**Lógica:** Se `jobId` informado, faz join com `candidate_embeddings` e `job_embeddings` para ranquear por similaridade.

---

### Task 10: Frontend — Dashboard IA

**Arquivos:**
- Modificar: `frontend/src/app/(dashboard)/page.tsx`

**Componentes:**
- Card "Score Médio" com gráfico de barra simples
- Card "Top Candidatos" com avatares + scores
- Card "Saúde do Pipeline" com % por estágio

---

### Task 11: Frontend — Busca semântica na listagem de candidatos

**Arquivos:**
- Modificar: `frontend/src/app/(dashboard)/candidates/page.tsx`
- Modificar: `frontend/src/app/(dashboard)/candidates/[id]/page.tsx`

**Features:**
- Botão "Buscar similares" no detalhe da vaga → redireciona para lista de candidatos ordenada por score
- Indicador de score via cor (verde > 70%, amarelo > 40%, vermelho < 40%)

---

### Task 12: Frontend — Recomendações no dashboard da vaga

**Arquivos:**
- Modificar: `frontend/src/app/(dashboard)/jobs/[id]/page.tsx`

**Features:**
- Seção "Candidatos recomendados" acima do pipeline com top 5 matches
- Cada card com nome, score, skills match

---

### Task 13: Frontend — Portal do candidato com recomendações

**Arquivos:**
- Criar: `frontend/src/app/candidate/jobs/page.tsx`
- Modificar: `frontend/src/app/candidate/profile/page.tsx`

**Features:**
- Página de vagas recomendadas para o candidato logado
- Botão "Vagas para você" no perfil

---

## Ordem de implementação

```
Migração GROQ (Task 1-2)
    ↓
Embeddings + pgvector (Task 3-4)
    ↓
Busca semântica (Task 5)
    ↓
Recomendações (Task 6-7)
    ↓
Dashboard IA (Task 8)
    ↓
Filtros inteligentes (Task 9)
    ↓
Frontend (Task 10-13)
```

## Riscos

- **GROQ rate limits:** tier gratuito tem limites. Implementar retry com backoff.
- **Embeddings 384d vs 1536d:** V16 criou `candidate_embeddings` com `vector(1536)` (OpenAI). Preciso de migration para `vector(384)` ou recriar. **Usar migration V22 para alterar dimensão.**
- **Performance pgvector:** Índice IVFFlat precisa de `lists` tuning. Para V2 com poucos dados, 100 lists é suficiente.
- **Custo GROQ:** modelos como mixtral-8x7b são gratuitos no tier dev. Monitorar uso.
