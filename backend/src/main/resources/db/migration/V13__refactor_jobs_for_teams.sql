-- V13__refactor_jobs_for_teams.sql
-- Jobs now belong to a Team (not directly to Company).
-- Adds Paused status and UUID v7 migration.

ALTER TABLE jobs ADD COLUMN IF NOT EXISTS team_id UUID REFERENCES teams(id);

-- Migrate existing jobs: assign to first team of the company (default team will be created at app level)
-- No data migration here — handled at application layer on first run.

CREATE INDEX idx_jobs_team ON jobs(team_id) WHERE deleted_at IS NULL;
