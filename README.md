# TalentFlowAI — SaaS de Recrutamento Inteligente com IA

Plataforma moderna de recrutamento que usa IA como copiloto para RH.

## Stack

| Camada | Tecnologia | Onde roda |
|--------|-----------|-----------|
| Frontend | Next.js 16 + React 19 + Tailwind 4 + shadcn/ui + React Query | Vercel |
| Backend | Spring Boot 3 + Java 21 | Railway / VPS |
| Banco | PostgreSQL + pgvector (Supabase) | Supabase |
| Cache/Fila | Redis | Docker (dev) / Upstash (prod) |
| Storage | Supabase Storage (S3-compatible) | Supabase |
| IA | OpenAI (embeddings + chat) | API |
| CI/CD | GitHub Actions | — |

## Arquitetura

```
┌─ Vercel ─────────────────────┐    ┌─ Railway/VPS ────────────────────┐
│  Next.js 16 (App Router)      │    │  Spring Boot 3 (Java 21)          │
│  shadcn/ui + Tailwind 4       │◄──►│  Clean Architecture + DDD         │
│  React Query + Axios + JWT    │    │  Spring Security + JWT            │
└───────────────────────────────┘    │  Flyway + JPA                     │
                                     └──────────┬───────────────────────┘
                                                │
                              ┌─────────────────▼───────────────────────┐
                              │  Supabase PostgreSQL + pgvector          │
                              │  ├─ Dados relacionais (tenants, vagas)   │
                              │  ├─ Vetores (embeddings OpenAI)          │
                              │  ├─ Histórico (audit trails)             │
                              │  └─ Storage (currículos, documentos)     │
                              └─────────────────────────────────────────┘
```

### Camadas do Backend (Clean Architecture)

```
Controller ──► Service ──► Repository (interface no domínio)
    │              │              │
Interface     Application      Domain (regras puras, zero framework)
(DTOs)        (use cases)      │
                                ▼
                           Infrastructure
                           (JPA, Security, AI, Storage)
```

## Quick Start

### 1. Supabase (PostgreSQL + pgvector)

1. Crie um projeto em https://supabase.com
2. No Dashboard: Database → Extensions → habilite **pgvector** (search "vector")
3. Copie a string de conexão (Project Settings → Database → Connection Pooling)

```bash
# .env na raiz do projeto
DATABASE_URL=jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres
DATABASE_USERNAME=postgres.<SEU_PROJECT_REF>
DATABASE_PASSWORD=<SUA_SENHA_DB>
JWT_SECRET=<segredo-64-chars>
OPENAI_API_KEY=sk-...
```

### 2. Redis (desenvolvimento local)

```bash
docker compose up -d   # Sobe Redis
```

### 3. Backend

```bash
cd backend
./gradlew bootRun      # Flyway roda migrations automaticamente
```

### 4. Frontend

```bash
cd frontend
npm install
npm run dev            # http://localhost:3000
```

## API Endpoints (MVP)

### Auth
```
POST /api/v1/auth/register   → Cria tenant + admin user + company
POST /api/v1/auth/login      → Retorna JWT com tenant_id
POST /api/v1/auth/refresh    → Renova access token
```

### Jobs (Vagas)
```
GET    /api/v1/jobs                  → Lista vagas do tenant
POST   /api/v1/jobs                  → Cria vaga + pipeline stages
GET    /api/v1/jobs/{id}             → Detalhe da vaga
PATCH  /api/v1/jobs/{id}/publish     → Publica vaga
PATCH  /api/v1/jobs/{id}/close       → Fecha vaga
DELETE /api/v1/jobs/{id}             → Remove vaga
```

### Pipeline
```
GET    /api/v1/jobs/{id}/pipeline         → Lista estágios
POST   /api/v1/jobs/{id}/pipeline         → Adiciona estágio
PUT    /api/v1/jobs/{id}/pipeline/reorder → Reordena (drag & drop)
```

### Candidates
```
GET    /api/v1/jobs/{id}/candidates                  → Lista candidatos
POST   /api/v1/jobs/{id}/candidates                  → Adiciona candidato
PATCH  /api/v1/jobs/{id}/candidates/{id}/move        → Move no pipeline
```

### Dashboard
```
GET /api/v1/jobs/dashboard → Stats do tenant
```

## Multi-Tenant

Cada empresa é um **tenant** isolado. O JWT carrega `tenant_id` nos claims. Toda query é automaticamente filtrada por tenant via `TenantContext` (ThreadLocal), garantindo que nenhum dado vaze entre tenants.

## pgvector — PostgreSQL como banco único

- **Dados relacionais**: tenants, users, jobs, candidates, interviews, audit_logs
- **Vetores**: `jobs.embedding` e `candidates.resume_embedding` (vector(1536))
- **Similaridade**: cosine similarity via `<=>` operator do pgvector
- **Índice**: IVFFlat para buscas eficientes

Nenhum banco vetorial separado. Tudo no mesmo PostgreSQL.
