-- V4__create_jobs.sql
-- Job postings with pgvector embedding for AI matching

CREATE TABLE jobs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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
CREATE INDEX idx_jobs_company ON jobs(company_id);
CREATE INDEX idx_jobs_created_by ON jobs(created_by);

COMMENT ON TABLE jobs IS 'Job postings. embedding column stores OpenAI text-embedding-3-small vectors for AI matching.';
COMMENT ON COLUMN jobs.type IS 'FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, REMOTE';
COMMENT ON COLUMN jobs.status IS 'DRAFT, PUBLISHED, CLOSED, ARCHIVED';
