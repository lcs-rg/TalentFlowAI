# Plano de Commits Atômicos — V1 (Sessão 2026-07-07)

> **Para Hermes:** Execute commits na ordem listada. Cada commit é atômico e funcional.

**Objetivo:** Versionar todo o trabalho implementado nesta sessão com commits atômicos,
conventional commits em português, separando backend e frontend.

**Padrão:** `tipo: descrição curta em português` (feat:, fix:, refactor:, chore:, docs:)

**Ordem:** Backend primeiro (migrations → domain → infra → application → presentation), depois frontend.

---

## Backend Commits

### Commit 1: Migration — corrigir schema de candidates para platform-level
```
feat: tornar candidates platform-level (job_id nullable + deleted_at/deleted_by)
```
**Arquivos:**
- `backend/src/main/resources/db/migration/V18__fix_candidates_platform_level.sql`

### Commit 2: Migration — criar tabela de notificações
```
feat: criar tabela notifications (V19)
```
**Arquivos:**
- `backend/src/main/resources/db/migration/V19__create_notifications.sql`

### Commit 3: Domain — adicionar softDelete ao CandidateRepository
```
feat: adicionar softDelete ao CandidateRepository
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/domain/recruitment/CandidateRepository.java`

### Commit 4: Domain — criar entidade Notification e repositório
```
feat: criar entidade Notification e NotificationRepository
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/domain/notification/Notification.java`
- `backend/src/main/java/com/talentflow/domain/notification/NotificationRepository.java`

### Commit 5: Infra — adicionar deletedBy ao CandidateJpaEntity + implementar softDelete
```
feat: adicionar deletedBy ao CandidateJpaEntity e implementar softDelete
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/infrastructure/persistence/AllRepositories.java`

### Commit 6: Infra — implementar NotificationJpaEntity e NotificationRepositoryImpl
```
feat: implementar persistência de notificações (JPA entity + repository impl)
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/infrastructure/persistence/NotificationPersistence.java`

### Commit 7: Infra — criar AIServiceClient (proxy para AI Service Python)
```
feat: criar AIServiceClient para comunicação com serviço Python de IA
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/infrastructure/ai/AIServiceClient.java`

### Commit 8: Config — adicionar ai-service.url no application.yml
```
feat: configurar URL do AI Service no application.yml
```
**Arquivos:**
- `backend/src/main/resources/application.yml`

### Commit 9: DTO — atualizar CandidateResponse para modelo platform-level
```
refactor: atualizar CandidateResponse para incluir resumeUrl, resumeText, applications
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/presentation/dto/response/CandidateResponse.java`

### Commit 10: Service — criar CandidateService com CRUD completo
```
feat: criar CandidateService com create, list, get, update e softDelete
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/application/recruitment/CandidateService.java`

### Commit 11: Service — adicionar getSessions ao AuthService
```
feat: adicionar método getSessions ao AuthService para listar sessões ativas
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/application/auth/AuthService.java`

### Commit 12: Controller — criar CandidateController (CRUD REST)
```
feat: criar CandidateController com list, create, get, update e delete
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/presentation/api/v1/CandidateController.java`

### Commit 13: Controller — criar CompanyController (GET/PUT)
```
feat: criar CompanyController com GET e PUT para dados da empresa
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/presentation/api/v1/CompanyController.java`

### Commit 14: Controller — criar AIController (match, parse-resume, screening)
```
feat: criar AIController com endpoints de match, parse-resume e screening
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/presentation/api/v1/AIController.java`

### Commit 15: Controller — criar InterviewController (CRUD + ai-questions)
```
feat: criar InterviewController com schedule, update, cancel e ai-questions
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/presentation/api/v1/InterviewController.java`

### Commit 16: Controller — criar NotificationController (list, markRead, markAllRead)
```
feat: criar NotificationController com listagem e marcação de leitura
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/presentation/api/v1/NotificationController.java`

### Commit 17: Controller — adicionar endpoint GET /auth/sessions
```
feat: adicionar endpoint GET /auth/sessions para listar sessões ativas
```
**Arquivos:**
- `backend/src/main/java/com/talentflow/presentation/api/v1/AuthController.java`

---

## Frontend Commits

### Commit 18: Página — listagem de candidatos com busca
```
feat: criar página de listagem de candidatos com busca
```
**Arquivos:**
- `frontend/src/app/(dashboard)/candidates/page.tsx`

### Commit 19: Página — formulário de novo candidato
```
feat: criar página de cadastro de novo candidato
```
**Arquivos:**
- `frontend/src/app/(dashboard)/candidates/new/page.tsx`

### Commit 20: Página — detalhe do candidato com candidaturas e IA Match
```
feat: criar página de detalhe do candidato com candidaturas e botão IA Match
```
**Arquivos:**
- `frontend/src/app/(dashboard)/candidates/[id]/page.tsx`

### Commit 21: Página — edição de dados da empresa
```
feat: criar página de edição de dados da empresa (nome, segmento, porte, site)
```
**Arquivos:**
- `frontend/src/app/(dashboard)/company/page.tsx`

### Commit 22: Página — listagem de entrevistas
```
feat: criar página de entrevistas com estado vazio
```
**Arquivos:**
- `frontend/src/app/(dashboard)/interviews/page.tsx`

### Commit 23: Refactor — atualizar pipeline kanban para usar endpoints de applications
```
refactor: migrar pipeline kanban para endpoints /applications com botão triagem IA
```
**Arquivos:**
- `frontend/src/app/(dashboard)/jobs/[id]/page.tsx`

---

## Resumo

| Camada | Commits |
|---|---|
| Backend — Migrations | 2 |
| Backend — Domain | 2 |
| Backend — Infra | 3 |
| Backend — Config | 1 |
| Backend — DTO | 1 |
| Backend — Service | 2 |
| Backend — Controller | 6 |
| **Backend total** | **17** |
| Frontend — Páginas | 6 |
| **Total** | **23** |
