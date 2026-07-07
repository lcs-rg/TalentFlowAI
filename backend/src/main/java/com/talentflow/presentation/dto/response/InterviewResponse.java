package com.talentflow.presentation.dto.response;
import java.time.Instant; import java.util.UUID;
public record InterviewResponse(UUID id, UUID candidateId, Instant scheduledAt, String type, String status, Instant createdAt) {}