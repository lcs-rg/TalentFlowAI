-- V3__create_companies.sql
-- Company profile within a tenant

CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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

COMMENT ON TABLE companies IS 'Company profile. One company per tenant (MVP).';
COMMENT ON COLUMN companies.size IS 'STARTUP, SMALL, MEDIUM, LARGE, ENTERPRISE';
