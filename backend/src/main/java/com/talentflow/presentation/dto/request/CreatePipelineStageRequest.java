package com.talentflow.presentation.dto.request;
import jakarta.validation.constraints.*;
public record CreatePipelineStageRequest(
    @NotBlank(message = "Nome do estágio é obrigatório") @Size(min = 2, max = 255) String name,
    @Pattern(regexp = "SCREENING|INTERVIEW|TECHNICAL|ASSESSMENT|OFFER|CUSTOM") String type,
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor inválida") String color
) {}