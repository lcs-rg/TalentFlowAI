package com.talentflow.presentation.dto.request;
import jakarta.validation.constraints.*; import java.time.Instant;
public record ScheduleInterviewRequest(@NotNull @Future Instant scheduledAt,
    @Pattern(regexp = "VIDEO|PHONE|IN_PERSON|TECHNICAL") String type) {}