# Banco de Dados

## Multi-Tenancy

Utilizar **Shared Database, Shared Schema**: todas as tabelas de negócio possuem coluna `tenant_id`.

Ativar **Row Level Security (RLS)** no PostgreSQL/Supabase em toda tabela que contenha `tenant_id`, como camada adicional de proteção — nunca como único mecanismo de isolamento.

A política de RLS deve validar `tenant_id` a partir do contexto de sessão/claim autenticado. Nunca aceitar `tenant_id` vindo diretamente de parâmetro de aplicação sem validação.

Nunca usar schema-per-tenant ou database-per-tenant nesta fase do projeto.

## Identificadores

Utilizar **UUID v7** como chave primária em todas as entidades principais, tanto interna quanto publicamente exposta.

Não manter identificador interno (BIGINT) separado do identificador público.

## Migrations

Utilizar **Flyway** para versionamento de schema.

- Toda alteração de schema deve ser feita via migration versionada. Nunca alteração manual direta no banco de produção.
- Migrations devem ser **idempotentes** e nunca destrutivas sem uma etapa reversível prévia (ex: nunca fazer `DROP COLUMN` na mesma migration que remove o último uso do campo no código).

## Exclusão de Dados (LGPD)

- Utilizar **soft delete** (`deleted_at`) como padrão para operações de exclusão de registros de negócio.
- Implementar um mecanismo de **anonimização** (job assíncrono) para atender solicitações de exclusão de dados pelo titular (LGPD), substituindo PII (nome, e-mail, telefone, conteúdo de currículo) por valores anonimizados, preservando o registro para integridade de histórico e métricas agregadas.
- Nunca implementar hard delete como única forma de exclusão de dados pessoais.

## Auditoria

- Utilizar uma tabela única e genérica **`audit_log`** para registrar ações sobre qualquer entidade, contendo no mínimo: `tenant_id`, `entity_type`, `entity_id`, `action`, `actor_id`, `diff` (ou payload da alteração) e `created_at`.
- Não criar tabelas de auditoria específicas por entidade.
- Prever particionamento por data na tabela `audit_log` como evolução futura (V2/V3). Não implementar na V1.

## IA e Embeddings

- Armazenar embeddings de IA (pgvector) em **tabela dedicada** (ex: `candidate_embeddings`), nunca como coluna dentro das tabelas principais de domínio (ex: `candidate`).
- A tabela de embeddings deve referenciar a entidade de origem por ID, permitindo reprocessamento ou troca de estratégia de embedding sem impacto na tabela de domínio.

## Convenções

- Nomear tabelas no **plural** e em **snake_case**.
- Toda tabela de negócio deve conter, no mínimo: `id`, `tenant_id` (quando aplicável), `created_at`, `updated_at`, `deleted_at`.

## Performance e Operação

- Definir índices para toda coluna usada em filtro frequente, especialmente `tenant_id` e chaves estrangeiras.
- Utilizar **connection pooling** (ex: PgBouncer ou pool nativo do Supabase) desde a V1.
- Definir rotina de backup com **point-in-time recovery** habilitado desde o primeiro ambiente de produção.
