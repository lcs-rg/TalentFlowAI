package com.talentflow.presentation.dto.request;
import jakarta.validation.constraints.*; import java.util.UUID;
public record MoveCandidateRequest(@NotNull UUID stageId) {}