-- V9__add_soft_delete_jobs.sql
-- Soft delete for jobs: instead of physical deletion, mark as deleted.
-- Enables audit trail and undo capability.

ALTER TABLE jobs ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS deleted_by UUID REFERENCES users(id);

CREATE INDEX IF NOT EXISTS idx_jobs_deleted ON jobs(deleted_at) WHERE deleted_at IS NOT NULL;

COMMENT ON COLUMN jobs.deleted_at IS 'Soft delete timestamp. NULL means active.';
COMMENT ON COLUMN jobs.deleted_by IS 'User who soft-deleted this job.';
