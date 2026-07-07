package com.talentflow.presentation.dto.request;
import jakarta.validation.constraints.*;
public record CreateJobRequest(
    @NotBlank(message = "Título é obrigatório") String title,
    @NotBlank(message = "Descrição é obrigatória") String description,
    String department, String location,
    String type,
    Integer salaryMin, Integer salaryMax, String currency
) {}
