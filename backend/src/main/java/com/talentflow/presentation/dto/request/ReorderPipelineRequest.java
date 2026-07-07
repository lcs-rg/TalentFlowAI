package com.talentflow.presentation.dto.request;
import jakarta.validation.constraints.*;
import java.util.List; import java.util.UUID;
public record ReorderPipelineRequest(@NotNull @Size(min = 1) List<UUID> stageIds) {}