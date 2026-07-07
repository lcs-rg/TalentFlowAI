-- V1__create_tenants.sql
-- Multi-tenant foundation: every tenant is an isolated company account.
--
-- Supabase: extensions are pre-enabled via Dashboard → Database → Extensions.
-- CREATE EXTENSION IF NOT EXISTS is idempotent — safe to run on any PostgreSQL.

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "vector";       -- pgvector for AI embeddings
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    slug VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    plan VARCHAR(50) NOT NULL DEFAULT 'FREE',
    settings JSONB DEFAULT '{}',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

COMMENT ON TABLE tenants IS 'Multi-tenant accounts. Each company gets one tenant.';
COMMENT ON COLUMN tenants.plan IS 'FREE, PRO, ENTERPRISE';
COMMENT ON COLUMN tenants.status IS 'ACTIVE, SUSPENDED, DELETED';
