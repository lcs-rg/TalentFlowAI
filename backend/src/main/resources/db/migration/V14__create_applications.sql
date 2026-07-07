-- V14__create_applications.sql
-- Application is the business entity linking a Candidate to a Job.
-- Replaces the old direct Candidate-to-Job relationship.

CREATE TABLE applications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES companies(id),
    team_id UUID NOT NULL REFERENCES teams(id),
    job_id UUID NOT NULL REFERENCES jobs(id),
    candidate_id UUID NOT NULL REFERENCES candidates(id),
    stage_id UUID REFERENCES pipeline_stages(id),
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    score REAL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    UNIQUE(job_id, candidate_id)
);

CREATE INDEX idx_applications_job ON applications(job_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_applications_candidate ON applications(candidate_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_applications_company ON applications(company_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_applications_stage ON applications(stage_id) WHERE deleted_at IS NULL;
