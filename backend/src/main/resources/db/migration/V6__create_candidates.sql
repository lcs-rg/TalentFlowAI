-- V6__create_candidates.sql
-- Candidates in the hiring pipeline, resumes with pgvector embeddings

CREATE TABLE candidates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    resume_url TEXT,
    resume_text TEXT,
    resume_embedding vector(1536),
    stage_id UUID REFERENCES pipeline_stages(id),
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    score REAL,
    tags JSONB DEFAULT '[]',
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX idx_candidates_job_stage ON candidates(job_id, stage_id);
CREATE INDEX idx_candidates_status ON candidates(status);
CREATE INDEX idx_candidates_email ON candidates(email);

COMMENT ON TABLE candidates IS 'Candidates in the hiring pipeline. resume_embedding stores OpenAI vector for AI matching.';
COMMENT ON COLUMN candidates.status IS 'NEW, SCREENING, INTERVIEWING, OFFERED, HIRED, REJECTED, WITHDRAWN';
COMMENT ON COLUMN candidates.score IS 'AI compatibility score (0.0 to 1.0)';
