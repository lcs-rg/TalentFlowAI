-- V7__create_interviews.sql
-- Interview scheduling, AI-generated questions, and feedback

CREATE TABLE interviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    candidate_id UUID NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
    scheduled_at TIMESTAMPTZ,
    type VARCHAR(50) NOT NULL DEFAULT 'VIDEO',
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    ai_questions JSONB DEFAULT '[]',
    feedback JSONB DEFAULT '{}',
    recording_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX idx_interviews_candidate ON interviews(candidate_id);
CREATE INDEX idx_interviews_scheduled ON interviews(scheduled_at) WHERE status = 'SCHEDULED';

COMMENT ON TABLE interviews IS 'Interview records with AI-generated questions and structured feedback.';
COMMENT ON COLUMN interviews.type IS 'VIDEO, PHONE, IN_PERSON, TECHNICAL';
COMMENT ON COLUMN interviews.status IS 'SCHEDULED, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW';
COMMENT ON COLUMN interviews.ai_questions IS 'JSON array of AI-generated questions: [{"question":"...", "category":"technical", "difficulty":"medium"}]';
COMMENT ON COLUMN interviews.feedback IS 'JSON: {"strengths":[], "weaknesses":[], "overall_rating":4, "notes":"...", "hire_recommendation":true}';
