package com.talentflow.domain.identity;

public enum Role {
    ADMIN("Full access — manage company, users, billing"),
    RECRUITER("Manage jobs, candidates, interviews"),
    HIRING_MANAGER("Review candidates, give feedback, approve hires"),
    VIEWER("Read-only access to jobs and pipeline");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
