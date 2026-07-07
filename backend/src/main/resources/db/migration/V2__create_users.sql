-- V2__create_users.sql
-- Users within a tenant, with RBAC roles

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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
CREATE INDEX idx_users_email ON users(email);

COMMENT ON TABLE users IS 'Users scoped to a tenant. Auth via Spring Security + JWT.';
COMMENT ON COLUMN users.role IS 'ADMIN, RECRUITER, HIRING_MANAGER, VIEWER';
