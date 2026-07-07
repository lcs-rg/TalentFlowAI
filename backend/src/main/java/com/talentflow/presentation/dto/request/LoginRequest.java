package com.talentflow.presentation.dto.request;

import jakarta.validation.constraints.*;

public record LoginRequest(
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    String password,

    @NotBlank(message = "Slug da empresa é obrigatório")
    String tenantSlug
) {}
