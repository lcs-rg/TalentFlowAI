-- V10__add_soft_delete_pipeline.sql
-- Extend soft delete to pipeline stages and candidates.

ALTER TABLE pipeline_stages ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS deleted_by UUID REFERENCES users(id);

CREATE INDEX IF NOT EXISTS idx_pipeline_deleted ON pipeline_stages(deleted_at) WHERE deleted_at IS NOT NULL;
