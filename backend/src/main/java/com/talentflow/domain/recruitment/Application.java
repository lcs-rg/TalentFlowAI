package com.talentflow.domain.recruitment;

import com.talentflow.domain.shared.BaseEntity;
import java.time.Instant;
import java.util.*;

public class Application extends BaseEntity {
    private final UUID companyId;
    private final UUID teamId;
    private final UUID jobId;
    private final UUID candidateId;
    private UUID stageId;
    private ApplicationStatus status;
    private Float score;
    private List<String> feedback;

    public Application(UUID id, UUID companyId, UUID teamId, UUID jobId, UUID candidateId) {
        super(id);
        this.companyId = companyId;
        this.teamId = teamId;
        this.jobId = jobId;
        this.candidateId = candidateId;
        this.status = ApplicationStatus.NEW;
        this.feedback = new ArrayList<>();
    }

    public void moveToStage(UUID newStageId) { this.stageId = newStageId; markUpdated(); }
    public void updateStatus(ApplicationStatus s) { this.status = s; markUpdated(); }
    public void setScore(float s) { this.score = Math.max(0, Math.min(1, s)); markUpdated(); }
    public void addFeedback(String note) { this.feedback.add(note); markUpdated(); }

    public UUID getCompanyId() { return companyId; }
    public UUID getTeamId() { return teamId; }
    public UUID getJobId() { return jobId; }
    public UUID getCandidateId() { return candidateId; }
    public UUID getStageId() { return stageId; }
    public ApplicationStatus getStatus() { return status; }
    public Float getScore() { return score; }
    public List<String> getFeedback() { return Collections.unmodifiableList(feedback); }
}
