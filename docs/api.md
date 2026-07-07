# API REST

## Base URL

```
Produção: https://api.talentflow.ai/api/v1
Dev:      http://localhost:8080/api/v1
```

## Autenticação

Todas as rotas (exceto `/auth/*` e `/public/*`) exigem header:

```
Authorization: Bearer <access_token>
```

### JWT Claims (Access Token)

```json
{
  "sub": "user-uuid",
  "tenant_id": "tenant-uuid",
  "email": "user@email.com",
  "role": "ADMIN",
  "permissions": ["job:create", "job:publish", "candidate:view", "candidate:delete"],
  "exp": 1719000000
}
```

`role` é usado apenas para exibição/UX no client. Toda decisão de autorização no backend deve se basear em **`permissions`**, nunca em `role` isoladamente.

### Refresh Token

Refresh token é **opaco** (não-JWT), enviado via cookie `httpOnly`, associado a `user_id` + `device_id` no banco.

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/v1/auth/register` | Cria tenant + admin + company |
| POST | `/api/v1/auth/login` | Login com email + senha + tenantSlug |
| POST | `/api/v1/auth/refresh` | Rotaciona refresh token e emite novo access token |
| POST | `/api/v1/auth/logout` | Revoga o refresh token do dispositivo atual |
| POST | `/api/v1/auth/logout-all` | Revoga todos os refresh tokens do usuário (logout global) |
| GET | `/api/v1/auth/sessions` | Lista dispositivos/sessões ativas do usuário |

### Headers de Resposta

```
X-API-Version: 1.0.0
X-Correlation-ID: uuid-gerado-ou-recebido
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 87
X-RateLimit-Reset: 1719000600
```

### Idempotency

Endpoints que disparam efeito colateral não-idempotente (ex: convite de time, criação de entrevista) aceitam header opcional:

```
Idempotency-Key: <uuid gerado pelo client>
```

Requisições repetidas com a mesma chave dentro de 24h retornam a resposta original, sem duplicar o efeito.

## Envelope Padrão

Toda resposta segue `ApiResponse<T>`:

```json
{
  "success": true,
  "data": { ... },
  "meta": { "page": 0, "size": 20, "total": 150, "totalPages": 8 },
  "error": null,
  "timestamp": "2026-07-02T00:00:00Z"
}
```

Erro:

```json
{
  "success": false,
  "data": null,
  "error": {
    "status": 400,
    "code": "VALIDATION_ERROR",
    "message": "Erro de validação",
    "errors": [
      { "field": "title", "message": "Título é obrigatório" }
    ]
  },
  "timestamp": "..."
}
```

**`code` é obrigatório** em todo erro — é o campo que o frontend usa para mapear mensagens amigáveis. `message` é auxiliar/debug, nunca a fonte de verdade para lógica de UI.

## Identificadores

Todo ID de recurso é **UUID v7**, tanto na URL quanto no payload.

## Multi-Tenancy

`tenant_id` nunca aparece na URL nem é aceito via payload. É sempre extraído do token autenticado no backend.

---

## Endpoints

### Company / Team

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/v1/company` | Dados da empresa (tenant atual) |
| PUT | `/api/v1/company` | Atualiza dados da empresa |
| GET | `/api/v1/team` | Lista membros do time |
| POST | `/api/v1/team/invite` | Convida novo membro (requer `Idempotency-Key`) |
| PATCH | `/api/v1/team/{userId}/role` | Altera role/permissions de um membro |
| DELETE | `/api/v1/team/{userId}` | Remove membro do time (soft delete) |

### Jobs (Vagas)

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/v1/jobs` | Lista paginada (query: status, search, type) |
| POST | `/api/v1/jobs` | Cria vaga + estágios default |
| GET | `/api/v1/jobs/{id}` | Detalhe da vaga |
| PUT | `/api/v1/jobs/{id}` | Atualiza vaga |
| PATCH | `/api/v1/jobs/{id}/publish` | Publica vaga (draft → published) |
| PATCH | `/api/v1/jobs/{id}/close` | Fecha vaga |
| DELETE | `/api/v1/jobs/{id}` | Soft delete |
| GET | `/api/v1/jobs/{id}/pipeline` | Lista estágios ordenados |
| POST | `/api/v1/jobs/{id}/pipeline` | Adiciona estágio |
| PUT | `/api/v1/jobs/{id}/pipeline/reorder` | Reordena estágios |
| GET | `/api/v1/jobs/dashboard` | Métricas: activeJobs, totalCandidates |

### Candidates (Perfil Global)

Candidato é uma entidade independente da vaga — representa a pessoa, não a candidatura.

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/v1/candidates` | Lista paginada de candidatos do tenant |
| POST | `/api/v1/candidates` | Cria perfil de candidato |
| GET | `/api/v1/candidates/{id}` | Detalhe (inclui histórico de applications) |
| PUT | `/api/v1/candidates/{id}` | Atualiza dados do candidato |
| POST | `/api/v1/candidates/{id}/resume` | Upload de currículo. Retorna **URL assinada**, nunca path direto |
| DELETE | `/api/v1/candidates/{id}` | Soft delete + anonimização quando aplicável (LGPD) |

### Applications (Vínculo Candidato ↔ Vaga)

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/v1/jobs/{jobId}/applications` | Lista paginada (query: status, stageId, search) |
| POST | `/api/v1/jobs/{jobId}/applications` | Associa candidato existente (ou cria e associa) à vaga |
| GET | `/api/v1/applications/{id}` | Detalhe (estágio, status, histórico, feedbacks) |
| PATCH | `/api/v1/applications/{id}/move` | Move entre estágios do pipeline |
| POST | `/api/v1/applications/{id}/feedback` | Adiciona nota/feedback interno |

### Interviews

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/v1/applications/{id}/interviews` | Lista entrevistas da application |
| POST | `/api/v1/applications/{id}/interviews` | Agenda entrevista (requer `Idempotency-Key`) |
| PATCH | `/api/v1/interviews/{id}` | Atualiza/reagenda entrevista |
| DELETE | `/api/v1/interviews/{id}` | Cancela entrevista |
| POST | `/api/v1/interviews/{id}/ai-questions` | Gera perguntas via IA |

### Notifications

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/v1/notifications` | Lista notificações do usuário (query: unreadOnly) |
| PATCH | `/api/v1/notifications/{id}/read` | Marca como lida |
| PATCH | `/api/v1/notifications/read-all` | Marca todas como lidas |

### IA

Todos os endpoints abaixo são mediados pelo backend (Spring Boot), que internamente chama o AI Service. O client **nunca** acessa o AI Service diretamente.

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/v1/ai/match/{jobId}/{candidateId}` | Score de aderência |
| POST | `/api/v1/ai/parse-resume` | Extração de dados do currículo |
| POST | `/api/v1/ai/screening/{jobId}` | Triagem IA de todas as applications da vaga |

---

## Paginação

Query params: `?page=0&size=20&sort=createdAt,desc`

Resposta inclui `meta`:
```json
{ "meta": { "page": 0, "size": 20, "total": 150, "totalPages": 8 } }
```

## Erros HTTP

| Código | Significado |
|--------|------------|
| 400 | Validação / argumento inválido |
| 401 | Token inválido ou expirado |
| 403 | Sem permissão (permission ausente no token) |
| 404 | Recurso não encontrado |
| 409 | Conflito de estado (ex: publicar vaga já publicada) |
| 429 | Rate limit excedido |
| 500 | Erro interno |
