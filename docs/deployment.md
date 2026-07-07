# Deployment e Infraestrutura

## Visão Geral dos Serviços

O sistema é composto por três serviços deployados de forma independente:

- **Frontend (Next.js)** — hospedado na **Vercel**.
- **Core API (Spring Boot)** — hospedado na **Railway**, como contêiner Docker.
- **AI Service (FastAPI/Python)** — hospedado na **Railway**, como contêiner Docker independente do Core API.
- **Banco de Dados e Storage** — **Supabase** (PostgreSQL + pgvector + Storage), gerenciado.

Essa decisão é válida para a V1. Quando volume de tráfego, requisitos de compliance de cliente enterprise, ou necessidade de VPC dedicada justificarem, migrar Spring Boot e AI Service para AWS (ECS/Fargate). Essa migração não deve ser antecipada sem necessidade real (YAGNI também vale para infraestrutura).

## Containerização

- Cada serviço (Core API, AI Service) possui seu próprio `Dockerfile`, buildado e publicado no pipeline de CI antes do deploy.
- Utilizar `docker-compose` apenas para ambiente de desenvolvimento local. Nunca como estratégia de produção.
- Imagens Docker devem ser escaneadas por vulnerabilidades (**Trivy**) no pipeline de CI antes de qualquer deploy.

## Ambientes

Três ambientes: `development` (local), `staging` e `production`.

- Cada ambiente possui seu próprio conjunto de variáveis de ambiente e credenciais. Nunca compartilhadas entre ambientes.
- `staging` recebe deploy automático a cada merge na branch principal.
- `production` exige aprovação manual (tag de release).

## Secrets

- Toda credencial (chaves de IA, credenciais de banco, JWT secret) é armazenada no gerenciador de variáveis de ambiente da própria plataforma de hosting (Railway/Vercel). Nunca commitada no repositório.
- Rotação de secrets críticos (JWT signing key, chaves de provedores de IA) deve ser um processo documentado, não ad-hoc.

## Rede e Borda

- Utilizar **Cloudflare** (ou equivalente) na frente do Core API e AI Service para: TLS automático, proteção básica contra DDoS, e como camada de rate limiting de infraestrutura.
- Toda comunicação entre Frontend, Core API, AI Service e Banco deve ocorrer via **HTTPS/TLS**, mesmo internamente entre serviços na mesma plataforma.

## Migrations e Deploy

- Migrations do Flyway rodam como etapa obrigatória do pipeline de deploy, antes de a nova versão do Core API receber tráfego. Nunca executar migration manualmente em produção.
- Deploy de nova versão só deve receber tráfego após o health check (`/actuator/health`) reportar sucesso (deploy gated por health check, não por tempo fixo de espera).

## Rollback

- Toda plataforma de hosting utilizada deve suportar rollback para a versão anterior de forma rápida (redeploy de imagem anterior).
- Rollback de schema (migration) é tratado separadamente e deve seguir estratégia de migrations reversíveis. Nunca depender de rollback automático de schema.

## Domínio e DNS

Domínio principal apontado via Cloudflare, com subdomínios separados para API (`api.talentflow.ai`) e frontend (`app.talentflow.ai` ou raiz).

## Backup e Disaster Recovery

- Backup de banco com **point-in-time recovery** gerenciado pelo Supabase.
- Documentar (mesmo que manualmente na V1) um runbook básico de recuperação: como restaurar banco, como reverter deploy, quem aciona em caso de incidente.
