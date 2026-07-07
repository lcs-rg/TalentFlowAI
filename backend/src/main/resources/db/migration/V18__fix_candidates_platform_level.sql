-- V18: Align candidates table with platform-level domain model
-- Candidate is platform-level (not tied to a job). Applications link candidates to jobs.

-- Make job_id nullable (applications table handles the link)
ALTER TABLE candidates ALTER COLUMN job_id DROP NOT NULL;

-- Add deleted_at for soft delete (LGPD compliance)
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;

-- Add deleted_by for audit trail
ALTER TABLE candidates ADD COLUMN IF NOT EXISTS deleted_by UUID;
