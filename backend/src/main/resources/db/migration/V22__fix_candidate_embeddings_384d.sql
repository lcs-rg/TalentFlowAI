-- V22: Change candidate_embeddings vector dimension from 1536 (OpenAI) to 384 (all-MiniLM-L6-v2)
-- Drop and recreate since pgvector doesn't support ALTER COLUMN TYPE for vectors with different dims

DROP TABLE IF EXISTS candidate_embeddings CASCADE;

CREATE TABLE candidate_embeddings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    candidate_id UUID NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
    embedding vector(384),
    model_version VARCHAR(50) NOT NULL DEFAULT 'all-MiniLM-L6-v2',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(candidate_id)
);

CREATE INDEX IF NOT EXISTS idx_candidate_embeddings_vector
    ON candidate_embeddings USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);
