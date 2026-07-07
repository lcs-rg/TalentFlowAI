package com.talentflow.presentation.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CandidateResponse(
    UUID id,
    String name,
    String email,
    String phone,
    String resumeUrl,
    String resumeText,
    List<String> tags,
    String notes,
    Instant createdAt,
    Instant updatedAt,
    List<ApplicationSummary> applications
) {
    public record ApplicationSummary(UUID id, UUID jobId, String status, UUID stageId) {}
}
