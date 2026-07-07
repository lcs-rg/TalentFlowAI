package com.talentflow.presentation.dto.request;
import jakarta.validation.constraints.*;
public record CreateJobRequest(
    @NotBlank(message = "Título é obrigatório") @Size(min = 5, max = 255) String title,
    @NotBlank(message = "Descrição é obrigatória") @Size(min = 20, max = 10000) String description,
    @Size(max = 100) String department, @Size(max = 255) String location,
    @Pattern(regexp = "FULL_TIME|PART_TIME|CONTRACT|INTERNSHIP|REMOTE") String type,
    @Min(0) Integer salaryMin, @Min(0) Integer salaryMax, String currency
) {}