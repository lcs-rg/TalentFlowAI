-- V16__create_candidate_embeddings.sql
-- Embeddings stored in dedicated table (not as column in candidates).
-- Per database.md: IA embeddings never in main domain tables.

CREATE TABLE candidate_embeddings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    candidate_id UUID NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
    embedding vector(1536) NOT NULL,
    model VARCHAR(100) NOT NULL DEFAULT 'text-embedding-3-small',
    chunk_index INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_candidate_embeddings_candidate ON candidate_embeddings(candidate_id);
CREATE INDEX idx_candidate_embeddings_vector ON candidate_embeddings USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

-- Also create job_embeddings table for consistency
CREATE TABLE job_embeddings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    embedding vector(1536) NOT NULL,
    model VARCHAR(100) NOT NULL DEFAULT 'text-embedding-3-small',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_job_embeddings_job ON job_embeddings(job_id);
CREATE INDEX idx_job_embeddings_vector ON job_embeddings USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
