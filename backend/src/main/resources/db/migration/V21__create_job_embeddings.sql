-- V21: Create job_embeddings table for semantic search
CREATE TABLE IF NOT EXISTS job_embeddings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    embedding vector(384),
    model_version VARCHAR(50) NOT NULL DEFAULT 'all-MiniLM-L6-v2',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(job_id)
);

CREATE INDEX IF NOT EXISTS idx_job_embeddings_vector
    ON job_embeddings USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);
