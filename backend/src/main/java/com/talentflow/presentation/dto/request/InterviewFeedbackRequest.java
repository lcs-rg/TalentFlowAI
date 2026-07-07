package com.talentflow.presentation.dto.request;
import jakarta.validation.constraints.*; import java.util.Map;
public record InterviewFeedbackRequest(@NotNull Map<String, Object> feedback) {}