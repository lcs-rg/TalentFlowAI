package com.talentflow.presentation.dto.response;
import java.time.Instant; import java.util.UUID;
public record JobResponse(UUID id, String title, String description, String department, String location,
    String type, Integer salaryMin, Integer salaryMax, String currency, String status,
    Instant createdAt, Instant updatedAt, Instant publishedAt, long candidateCount) {}