-- V5__create_pipeline_stages.sql
-- Customizable hiring pipeline stages per job

CREATE TABLE pipeline_stages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    order_index INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'CUSTOM',
    color VARCHAR(7),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(job_id, order_index)
);

CREATE INDEX idx_pipeline_job ON pipeline_stages(job_id);

COMMENT ON TABLE pipeline_stages IS 'Hiring pipeline stages. Order determines Kanban column position.';
COMMENT ON COLUMN pipeline_stages.type IS 'SCREENING, INTERVIEW, TECHNICAL, ASSESSMENT, OFFER, CUSTOM';
