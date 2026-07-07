package com.talentflow.presentation.dto.response;
import java.util.UUID;
public record PipelineStageResponse(UUID id, String name, int orderIndex, String type, String color) {}