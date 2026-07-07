package com.talentflow.domain.recruitment;

import com.talentflow.domain.shared.BaseEntity;

import java.time.Instant;
import java.util.*;

/**
 * Interview entity — scheduled interview with AI-generated questions and feedback.
 */
public class Interview extends BaseEntity {

    private final UUID candidateId;
    private Instant scheduledAt;
    private InterviewType type;
    private InterviewStatus status;
    private List<InterviewQuestion> aiQuestions;
    private Map<String, Object> feedback;

    public Interview(UUID id, UUID candidateId, InterviewType type) {
        super(id);
        this.candidateId = candidateId;
        this.type = type != null ? type : InterviewType.VIDEO;
        this.status = InterviewStatus.SCHEDULED;
        this.aiQuestions = new ArrayList<>();
        this.feedback = new HashMap<>();
    }

    // --- Business rules ---

    public void schedule(Instant scheduledAt) {
        if (scheduledAt.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Cannot schedule interview in the past");
        }
        this.scheduledAt = scheduledAt;
        this.status = InterviewStatus.SCHEDULED;
        markUpdated();
    }

    public void confirm() {
        if (this.status != InterviewStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled interviews can be confirmed");
        }
        this.status = InterviewStatus.CONFIRMED;
        markUpdated();
    }

    public void complete(Map<String, Object> feedback) {
        this.status = InterviewStatus.COMPLETED;
        this.feedback = new HashMap<>(feedback);
        markUpdated();
    }

    public void cancel() {
        if (this.status == InterviewStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed interview");
        }
        this.status = InterviewStatus.CANCELLED;
        markUpdated();
    }

    public void setAiQuestions(List<InterviewQuestion> questions) {
        this.aiQuestions = new ArrayList<>(questions);
        markUpdated();
    }

    // --- Getters ---

    public UUID getCandidateId() { return candidateId; }
    public Instant getScheduledAt() { return scheduledAt; }
    public InterviewType getType() { return type; }
    public InterviewStatus getStatus() { return status; }
    public List<InterviewQuestion> getAiQuestions() { return Collections.unmodifiableList(aiQuestions); }
    public Map<String, Object> getFeedback() { return Collections.unmodifiableMap(feedback); }

    /**
     * Value object for an AI-generated interview question.
     */
    public record InterviewQuestion(String question, String category, String difficulty) {}
}
