-- V12__create_company_users.sql
-- Associates a User with a Company and Team.

CREATE TABLE company_users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES companies(id),
    user_id UUID NOT NULL REFERENCES users(id),
    team_id UUID NOT NULL REFERENCES teams(id),
    role VARCHAR(50) NOT NULL DEFAULT 'RECRUITER',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    UNIQUE(company_id, user_id)
);

CREATE INDEX idx_company_users_company ON company_users(company_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_company_users_team ON company_users(team_id) WHERE deleted_at IS NULL;
