-- V8__create_audit_logs.sql
-- Immutable audit trail for compliance and debugging

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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
CREATE INDEX idx_audit_resource ON audit_logs(resource_type, resource_id);
CREATE INDEX idx_audit_user ON audit_logs(user_id, created_at DESC);

COMMENT ON TABLE audit_logs IS 'Immutable audit trail. Every state-changing action is logged.';
COMMENT ON COLUMN audit_logs.action IS 'CREATED, UPDATED, DELETED, STATUS_CHANGED, EXPORTED, etc.';
COMMENT ON COLUMN audit_logs.resource_type IS 'JOB, CANDIDATE, INTERVIEW, PIPELINE_STAGE, USER, etc.';
