# TalentFlowAI — Architecture & Implementation Plan v2

> **Stack:** Next.js (frontend) + Spring Boot (backend) + Supabase PostgreSQL/pgvector

**Goal:** SaaS de recrutamento inteligente. MVP focado no lado EMPRESA.

**Architecture:** Frontend thin client (Next.js + React Query + shadcn/ui) consumindo REST API Spring Boot. Backend com Clean Architecture + DDD, JPA/Hibernate, Flyway, Spring Security JWT, OpenAI + pgvector para IA.

---

## Architecture Decisions Log (ADL)

### ADL-001: Frontend-Backend Separation
**Decision:** Next.js (Vercel) + Spring Boot (Railway) como serviços independentes.
**Rationale:** Separação total de responsabilidades. Backend Java lida com regras de negócio complexas, transações, IA pesada e background jobs. Frontend Next.js foca em renderização, UX e performance na Vercel.
**Trade-off:** Dois deploys, CORS, dupla manutenção de tipos (mitigado com OpenAPI/contracts).

### ADL-002: Spring Security + JWT (sem Supabase Auth)
**Decision:** Autenticação 100% Spring Boot com JWT stateless. Supabase usamos só como PostgreSQL gerenciado + Storage.
**Rationale:** Controle total do fluxo de auth, RBAC customizado, tenant injection no JWT. Sem vendor lock-in no auth.
**Trade-off:** Mais código de auth para escrever vs. Supabase Auth pronto.

### ADL-003: JPA + Flyway
**Decision:** Spring Data JPA com Hibernate para ORM, Flyway para versionamento de schema.
**Rationale:** JPA maduro para queries complexas multi-tenant. Flyway versiona migrations de forma declarativa (SQL puro quando necessário).
**Trade-off:** Hibernate pode gerar queries ineficientes — usar native queries para operações críticas.

### ADL-004: Supabase Storage para currículos
**Decision:** Supabase Storage (S3-compatible) para upload de PDFs, DOCX e assets.
**Rationale:** Já está no ecossistema Supabase, S3-compatible, URLs públicas com políticas de acesso.
**Trade-off:** Vendor lock-in no Supabase Storage (mitigado: S3-compatible, migrável).

### ADL-005: pgvector nativo (sem biblioteca extra)
**Decision:** Queries pgvector via native SQL no Spring Data JPA.
**Rationale:** Extensão PostgreSQL madura. Sem dependência extra de biblioteca Java para vetores.
**Trade-off:** Queries manuais vs. abstração de biblioteca.

---

## Project Structure

```
talentflow/
├── frontend/                          # Next.js 16
│   ├── src/
│   │   ├── app/
│   │   │   ├── (auth)/                # Login, signup, forgot-password
│   │   │   │   ├── login/
│   │   │   │   ├── signup/
│   │   │   │   └── layout.tsx
│   │   │   ├── (dashboard)/           # Authenticated layout
│   │   │   │   ├── layout.tsx
│   │   │   │   ├── page.tsx           # Dashboard home
│   │   │   │   ├── jobs/
│   │   │   │   │   ├── page.tsx       # Lista de vagas
│   │   │   │   │   ├── new/
│   │   │   │   │   ├── [id]/
│   │   │   │   │   │   ├── page.tsx   # Detalhe da vaga
│   │   │   │   │   │   ├── pipeline/  # Kanban pipeline
│   │   │   │   │   │   └── edit/
│   │   │   │   ├── candidates/
│   │   │   │   │   ├── page.tsx
│   │   │   │   │   └── [id]/
│   │   │   │   ├── interviews/
│   │   │   │   ├── company/
│   │   │   │   ├── settings/
│   │   │   │   └── analytics/
│   │   │   ├── (public)/              # Páginas públicas
│   │   │   │   └── jobs/[id]/apply/
│   │   │   ├── layout.tsx             # Root layout
│   │   │   ├── globals.css
│   │   │   └── providers.tsx          # React Query, Theme, etc.
│   │   ├── components/
│   │   │   ├── ui/                    # shadcn/ui components
│   │   │   ├── layout/                # Sidebar, Navbar, Shell
│   │   │   ├── jobs/                  # JobCard, JobForm, JobFilters
│   │   │   ├── pipeline/              # KanbanBoard, StageColumn, CandidateCard
│   │   │   ├── candidates/            # CandidateTable, CandidateProfile
│   │   │   ├── interviews/            # InterviewScheduler, FeedbackForm
│   │   │   ├── analytics/             # Charts, Stats
│   │   │   └── shared/                # EmptyState, ErrorState, Loading
│   │   ├── hooks/
│   │   │   ├── use-jobs.ts            # React Query hooks
│   │   │   ├── use-candidates.ts
│   │   │   ├── use-pipeline.ts
│   │   │   ├── use-interviews.ts
│   │   │   ├── use-auth.ts
│   │   │   └── use-company.ts
│   │   ├── lib/
│   │   │   ├── api.ts                 # Axios/fetch wrapper com JWT
│   │   │   ├── auth.ts                # Token management
│   │   │   ├── validators.ts          # Zod schemas (shared com backend)
│   │   │   └── utils.ts
│   │   └── types/
│   │       ├── job.ts
│   │       ├── candidate.ts
│   │       ├── pipeline.ts
│   │       └── api.ts                 # API response types
│   ├── public/
│   ├── next.config.ts
│   ├── tailwind.config.ts
│   ├── components.json                # shadcn/ui config
│   ├── package.json
│   └── tsconfig.json
│
├── backend/                           # Spring Boot 3.x + Java 21
│   ├── src/main/java/com/talentflow/
│   │   ├── TalentFlowApplication.java
│   │   │
│   │   ├── config/                    # Configuration classes
│   │   │   ├── SecurityConfig.java
│   │   │   ├── CorsConfig.java
│   │   │   ├── RedisConfig.java
│   │   │   ├── OpenAIConfig.java
│   │   │   ├── SupabaseConfig.java
│   │   │   ├── JwtConfig.java
│   │   │   └── AsyncConfig.java
│   │   │
│   │   ├── domain/                    # DDD Domain Layer
│   │   │   ├── tenant/
│   │   │   │   ├── Tenant.java
│   │   │   │   ├── TenantPlan.java
│   │   │   │   └── TenantStatus.java
│   │   │   ├── company/
│   │   │   │   ├── Company.java
│   │   │   │   ├── CompanySize.java
│   │   │   │   └── Industry.java
│   │   │   ├── recruitment/
│   │   │   │   ├── Job.java
│   │   │   │   ├── JobStatus.java
│   │   │   │   ├── JobType.java
│   │   │   │   ├── PipelineStage.java
│   │   │   │   ├── Candidate.java
│   │   │   │   ├── CandidateStatus.java
│   │   │   │   ├── Interview.java
│   │   │   │   └── InterviewType.java
│   │   │   ├── identity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   └── Permission.java
│   │   │   ├── ai/
│   │   │   │   ├── Embedding.java
│   │   │   │   ├── MatchingResult.java
│   │   │   │   └── ScreeningQuestion.java
│   │   │   ├── audit/
│   │   │   │   └── AuditLog.java
│   │   │   └── shared/
│   │   │       ├── BaseEntity.java
│   │   │       ├── TenantAwareEntity.java
│   │   │       └── DomainEvent.java
│   │   │
│   │   ├── application/              # Application Layer (Use Cases)
│   │   │   ├── tenant/
│   │   │   │   ├── CreateTenantUseCase.java
│   │   │   │   └── TenantService.java
│   │   │   ├── company/
│   │   │   │   ├── CompanyService.java
│   │   │   │   └── dto/
│   │   │   ├── recruitment/
│   │   │   │   ├── JobService.java
│   │   │   │   ├── PipelineService.java
│   │   │   │   ├── CandidateService.java
│   │   │   │   ├── InterviewService.java
│   │   │   │   └── dto/
│   │   │   ├── ai/
│   │   │   │   ├── EmbeddingService.java
│   │   │   │   ├── MatchingService.java
│   │   │   │   ├── ScreeningAgent.java
│   │   │   │   ├── InterviewAgent.java
│   │   │   │   └── ResumeParserService.java
│   │   │   └── audit/
│   │   │       └── AuditService.java
│   │   │
│   │   ├── infrastructure/           # Infrastructure Layer
│   │   │   ├── persistence/
│   │   │   │   ├── TenantRepository.java
│   │   │   │   ├── CompanyRepository.java
│   │   │   │   ├── JobRepository.java
│   │   │   │   ├── PipelineStageRepository.java
│   │   │   │   ├── CandidateRepository.java
│   │   │   │   ├── InterviewRepository.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── AuditLogRepository.java
│   │   │   ├── ai/
│   │   │   │   ├── OpenAIClient.java
│   │   │   │   ├── PgVectorRepository.java
│   │   │   │   └── ResumeParser.java
│   │   │   ├── storage/
│   │   │   │   └── SupabaseStorageService.java
│   │   │   ├── security/
│   │   │   │   ├── JwtProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── TenantContext.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   └── messaging/
│   │   │       └── RedisPublisher.java
│   │   │
│   │   └── presentation/             # REST Controllers
│   │       ├── api/
│   │       │   ├── v1/
│   │       │   │   ├── AuthController.java
│   │       │   │   ├── JobController.java
│   │       │   │   ├── CandidateController.java
│   │       │   │   ├── PipelineController.java
│   │       │   │   ├── InterviewController.java
│   │       │   │   ├── CompanyController.java
│   │       │   │   ├── AIJobController.java
│   │       │   │   └── AnalyticsController.java
│   │       │   └── public/
│   │       │       └── PublicJobController.java
│   │       ├── dto/
│   │       │   ├── request/
│   │       │   └── response/
│   │       └── exception/
│   │           ├── GlobalExceptionHandler.java
│   │           └── ApiError.java
│   │
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   ├── application-prod.yml
│   │   └── db/migration/
│   │       ├── V1__create_tenants.sql
│   │       ├── V2__create_users.sql
│   │       ├── V3__create_companies.sql
│   │       ├── V4__create_jobs.sql
│   │       ├── V5__create_pipeline_stages.sql
│   │       ├── V6__create_candidates.sql
│   │       ├── V7__create_interviews.sql
│   │       ├── V8__create_audit_logs.sql
│   │       ├── V9__add_pgvector.sql
│   │       └── V10__seed_default_data.sql
│   │
│   ├── src/test/java/com/talentflow/
│   │   ├── unit/
│   │   ├── integration/
│   │   └── e2e/
│   │
│   ├── build.gradle                  # Gradle (Kotlin DSL)
│   ├── Dockerfile
│   └── settings.gradle
│
├── docker-compose.yml                # PostgreSQL + pgvector + Redis (dev)
├── .github/
│   └── workflows/
│       ├── backend-ci.yml
│       └── frontend-ci.yml
├── .gitignore
└── README.md
```

---

## Database Schema — Flyway Migrations

### V1: Tenants
```sql
CREATE TABLE tenants (
    id UUID PRIMARY KEY,
    slug VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    plan VARCHAR(50) NOT NULL DEFAULT 'FREE',
    settings JSONB DEFAULT '{}',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);
```

### V2: Users
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'RECRUITER',
    avatar_url TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    UNIQUE(tenant_id, email)
);

CREATE INDEX idx_users_tenant ON users(tenant_id);
```

### V3: Companies
```sql
CREATE TABLE companies (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    name VARCHAR(255) NOT NULL,
    logo_url TEXT,
    industry VARCHAR(100),
    size VARCHAR(50),
    website VARCHAR(500),
    settings JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);
```

### V4: Jobs (com embedding pgvector)
```sql
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE jobs (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    company_id UUID REFERENCES companies(id),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    department VARCHAR(100),
    location VARCHAR(255),
    type VARCHAR(50) NOT NULL DEFAULT 'FULL_TIME',
    salary_min INTEGER,
    salary_max INTEGER,
    currency VARCHAR(10) DEFAULT 'BRL',
    requirements JSONB DEFAULT '[]',
    benefits JSONB DEFAULT '[]',
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    embedding vector(1536),
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    published_at TIMESTAMPTZ,
    closed_at TIMESTAMPTZ
);

CREATE INDEX idx_jobs_tenant_status ON jobs(tenant_id, status);
CREATE INDEX idx_jobs_embedding ON jobs USING ivfflat (embedding vector_cosine_ops);
```

### V5: Pipeline Stages
```sql
CREATE TABLE pipeline_stages (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    order_index INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'CUSTOM',
    color VARCHAR(7),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(job_id, order_index)
);
```

### V6: Candidates
```sql
CREATE TABLE candidates (
    id UUID PRIMARY KEY,
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    resume_url TEXT,
    resume_text TEXT,
    resume_embedding vector(1536),
    stage_id UUID REFERENCES pipeline_stages(id),
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    score REAL,
    tags JSONB DEFAULT '[]',
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX idx_candidates_job_stage ON candidates(job_id, stage_id);
CREATE INDEX idx_candidates_status ON candidates(status);
CREATE INDEX idx_candidates_embedding ON candidates USING ivfflat (resume_embedding vector_cosine_ops);
```

### V7: Interviews
```sql
CREATE TABLE interviews (
    id UUID PRIMARY KEY,
    candidate_id UUID NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
    scheduled_at TIMESTAMPTZ,
    type VARCHAR(50) NOT NULL DEFAULT 'VIDEO',
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    ai_questions JSONB DEFAULT '[]',
    feedback JSONB DEFAULT '{}',
    recording_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);
```

### V8: Audit Logs
```sql
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    user_id UUID REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id UUID,
    old_value JSONB,
    new_value JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_tenant_action ON audit_logs(tenant_id, action, created_at DESC);
```

---

## API Contracts — REST Endpoints (MVP)

### Auth
```
POST   /api/v1/auth/login          → { email, password } → { accessToken, refreshToken, user }
POST   /api/v1/auth/register       → { email, password, name, companyName } → { accessToken, user }
POST   /api/v1/auth/refresh        → { refreshToken } → { accessToken }
GET    /api/v1/auth/me             → User (com tenant_id no JWT)
```

### Jobs (Vagas)
```
GET    /api/v1/jobs                → Page<JobResponse>      (filtro: status, department)
POST   /api/v1/jobs                → JobResponse
GET    /api/v1/jobs/{id}           → JobResponse
PUT    /api/v1/jobs/{id}           → JobResponse
PATCH  /api/v1/jobs/{id}/status    → JobResponse            (draft→published→closed)
DELETE /api/v1/jobs/{id}           → 204
```

### Pipeline
```
GET    /api/v1/jobs/{id}/pipeline  → List<PipelineStage>
POST   /api/v1/jobs/{id}/pipeline  → PipelineStage
PUT    /api/v1/pipeline/{id}       → PipelineStage
PATCH  /api/v1/pipeline/reorder    → void                   (drag & drop)
```

### Candidates
```
GET    /api/v1/jobs/{id}/candidates              → Page<CandidateResponse>
POST   /api/v1/jobs/{id}/candidates              → CandidateResponse
GET    /api/v1/candidates/{id}                   → CandidateResponse
PATCH  /api/v1/candidates/{id}/stage             → CandidateResponse    (mover no pipeline)
PATCH  /api/v1/candidates/{id}/status            → CandidateResponse
POST   /api/v1/candidates/{id}/resume            → CandidateResponse    (upload PDF)
POST   /api/v1/candidates/upload-resume          → ParsedResumeResponse (parsing IA)
```

### Interviews
```
GET    /api/v1/candidates/{id}/interviews        → List<InterviewResponse>
POST   /api/v1/candidates/{id}/interviews        → InterviewResponse
PATCH  /api/v1/interviews/{id}/feedback          → InterviewResponse
POST   /api/v1/interviews/{id}/ai-questions      → List<Question>       (gerar perguntas IA)
```

### AI
```
POST   /api/v1/ai/match/{jobId}/{candidateId}    → MatchingResultResponse
POST   /api/v1/ai/screen/{jobId}                 → ScreeningResultResponse (triar todos candidatos)
POST   /api/v1/ai/parse-resume                   → ParsedResumeResponse
GET    /api/v1/ai/job/{jobId}/insights           → JobInsightsResponse
```

### Public (sem auth)
```
GET    /api/public/jobs/{slug}                    → PublicJobResponse
POST   /api/public/jobs/{slug}/apply              → ApplicationResponse
```

---

## Multi-Tenant Strategy

### JWT Token Claims
```json
{
  "sub": "user-uuid",
  "tenant_id": "tenant-uuid",
  "role": "ADMIN",
  "permissions": ["JOBS:CREATE", "JOBS:DELETE", "CANDIDATES:VIEW"],
  "exp": 1719000000
}
```

### TenantContext (ThreadLocal)
```java
public class TenantContext {
    private static final ThreadLocal<UUID> currentTenant = new ThreadLocal<>();

    public static void setTenant(UUID tenantId) { currentTenant.set(tenantId); }
    public static UUID getTenant() { return currentTenant.get(); }
    public static void clear() { currentTenant.remove(); }
}
```

### JwtAuthenticationFilter — extrai tenant_id e injeta no TenantContext
```java
// No filter:
UUID tenantId = UUID.fromString(claims.get("tenant_id").toString());
TenantContext.setTenant(tenantId);

// No finally:
TenantContext.clear();
```

### BaseRepository — todas queries passam tenant_id
```java
@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    @Query("SELECT j FROM Job j WHERE j.tenantId = :#{T(com.talentflow.infrastructure.security.TenantContext).getTenant()}")
    Page<Job> findAllByTenant(Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.id = :id AND j.tenantId = :#{T(com.talentflow.infrastructure.security.TenantContext).getTenant()}")
    Optional<Job> findByIdAndTenant(UUID id);
}
```

---

## AI Architecture

```
┌──────────────────────────────────────────────────────┐
│                   AI Pipeline                         │
├──────────────────────────────────────────────────────┤
│                                                       │
│  1. Job Created                                       │
│     └─→ OpenAI Embedding (text-embedding-3-small)     │
│         └─→ Store in jobs.embedding (pgvector)        │
│                                                       │
│  2. Resume Uploaded                                   │
│     └─→ PDF Parser (Apache PDFBox)                    │
│         └─→ Chunk text into sections                  │
│             └─→ OpenAI Embedding per chunk            │
│                 └─→ Store in candidates.resume_embedding │
│                                                       │
│  3. Matching (Job ↔ Candidate)                        │
│     └─→ Cosine similarity (pgvector)                  │
│         └─→ Structured comparison (skills, experience) │
│             └─→ OpenAI Chat (explanation)             │
│                 └─→ Return score + explanation         │
│                                                       │
│  4. Screening Agent                                   │
│     └─→ OpenAI Chat with job context                  │
│         └─→ Generate screening questions              │
│             └─→ Score candidate responses              │
│                                                       │
│  5. Interview Agent                                   │
│     └─→ Job requirements + candidate resume           │
│         └−→ OpenAI Chat (structured output)           │
│             └─→ Technical + behavioral questions       │
│                                                       │
└──────────────────────────────────────────────────────┘
```

### OpenAI Models
| Uso | Modelo | Custo (input/1M tokens) |
|-----|--------|------------------------|
| Embeddings | text-embedding-3-small | $0.02 |
| Chat/Agents | gpt-4o-mini | $0.15 |
| Resume Parsing | gpt-4o-mini | $0.15 |
| Complex Analysis | gpt-4o | $2.50 |

---

## Frontend Component Architecture

### Data Flow
```
React Query Hook → API Client (axios + JWT interceptor) → Spring Boot REST
       ↓                                                        ↓
  Cached State                                           TenantContext
  (stale-while-revalidate)                               (ThreadLocal)
```

### Component Tree (Dashboard Shell)
```tsx
<QueryClientProvider>
  <ThemeProvider>
    <AuthProvider>
      <DashboardLayout>
        <Sidebar />           {/* Navegação: Vagas, Candidatos, Entrevistas */}
        <main>
          <Breadcrumb />
          {children}           {/* Page content */}
        </main>
      </DashboardLayout>
    </AuthProvider>
  </ThemeProvider>
</QueryClientProvider>
```

### Key Components
- **JobForm**: React Hook Form + Zod validation, rich text editor para descrição da vaga
- **KanbanBoard**: @dnd-kit para drag & drop entre estágios do pipeline
- **CandidateCard**: Card com score, tags, e ações rápidas
- **AIInsightPanel**: Painel lateral com análises de IA (match, sugestões)
- **ResumeUpload**: Upload com preview, parsing progress, dados extraídos

---

## Implementation Phases

### Phase 0: Foundation (3-4 dias)
**Backend:**
- Spring Boot project scaffold (Gradle, Java 21)
- Docker Compose (PostgreSQL + pgvector + Redis)
- Flyway migrations (V1-V8)
- JPA entities + repositories
- Spring Security + JWT config
- TenantContext + JWT filter
- Global exception handler
- CORS config

**Frontend:**
- Next.js project scaffold
- shadcn/ui init + theme
- Tailwind 4 config
- Axios client com JWT interceptor
- React Query provider
- Auth pages (login, signup)
- Dashboard shell (sidebar + layout)

### Phase 1: EMPRESA MVP Core (4-5 dias)
**Backend:**
- Auth endpoints (login, register, refresh, me)
- Company CRUD
- Job CRUD com status
- Pipeline stages CRUD
- Validações e regras de negócio
- Testes de integração

**Frontend:**
- Dashboard home com métricas
- Lista de vagas (tabela + filtros)
- Formulário de criação/edição de vaga (React Hook Form + Zod)
- Configuração de pipeline stages
- Empty states, loading skeletons, error states

### Phase 2: Candidate Pipeline (3-4 dias)
**Backend:**
- Candidate CRUD (por job)
- Upload de currículo (Supabase Storage)
- Parsing de currículo (PDFBox + OpenAI)
- Movimentação no pipeline
- Testes

**Frontend:**
- Kanban board (drag & drop pipeline)
- Candidate cards com informações
- Upload de currículo com preview
- Filtros e busca de candidatos
- Notas e avaliações manuais

### Phase 3: AI Screening Agent (4-5 dias)
**Backend:**
- OpenAI embedding service
- Embedding de jobs (ao criar/publicar)
- Embedding de currículos (ao fazer upload)
- Matching service (cosine similarity)
- Screening agent (perguntas adaptativas)
- Interview question agent
- Testes

**Frontend:**
- Score de compatibilidade no card do candidato
- Explicação do match (painel lateral)
- Botão "Triar com IA" no pipeline
- Perguntas geradas por IA na entrevista
- Indicadores de carregamento para operações IA

### Phase 4: CANDIDATO Side (4-5 dias)
**Backend:**
- Public job endpoints
- Public application endpoint
- Candidato self-service (perfil, currículos)
- Resume improver agent
- Job matching (busca reversa)
- Tracking de candidaturas

**Frontend:**
- Página pública da vaga (SEO otimizado)
- Formulário de candidatura público
- Portal do candidato (signup próprio)
- Currículo builder
- Sugestões de melhoria IA
- Tracking de processos

### Phase 5: Production Polish (3-4 dias)
- Notificações (email templates)
- Audit logs dashboard
- Rate limiting (Redis)
- API documentation (OpenAPI/Swagger)
- CI/CD (GitHub Actions)
- Dockerfile produção
- Testes E2E
- Observabilidade (logs estruturados, health checks)

---

## Verification per Phase

### Phase 0:
- [ ] `docker compose up` sobe PostgreSQL + Redis
- [ ] `./gradlew flywayMigrate` aplica V1-V8
- [ ] `POST /api/v1/auth/register` cria tenant + user + company
- [ ] `POST /api/v1/auth/login` retorna JWT com tenant_id
- [ ] Endpoint autenticado filtra por tenant automaticamente
- [ ] Next.js dev server conecta com backend

### Phase 1:
- [ ] CRUD completo de vagas com status
- [ ] Pipeline stages configuráveis
- [ ] Dashboard mostra dados reais
- [ ] Testes de integração passam para todos endpoints

### Phase 2:
- [ ] Upload de PDF → parse → embedding → candidate criado
- [ ] Drag & drop entre estágios
- [ ] Busca e filtros funcionais
- [ ] 80% test coverage nos serviços críticos

### Phase 3:
- [ ] Matching retorna score > 0.5 para candidatos relevantes
- [ ] Screening agent gera perguntas contextualizadas
- [ ] Embeddings são cacheados (sem re-gerar para mesmo texto)

---

**Next Action:** Executar Phase 0 — Foundation. Backend Spring Boot + Frontend Next.js.
