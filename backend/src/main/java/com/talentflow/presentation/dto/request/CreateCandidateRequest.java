package com.talentflow.presentation.dto.request;
import jakarta.validation.constraints.*;
public record CreateCandidateRequest(
    @NotBlank @Size(min = 2, max = 255) String name,
    @NotBlank @Email String email,
    @Pattern(regexp = "^$|^\\+?[0-9]{8,15}$", message = "Telefone inválido") String phone
) {}