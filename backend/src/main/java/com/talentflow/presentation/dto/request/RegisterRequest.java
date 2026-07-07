package com.talentflow.presentation.dto.request;

import jakarta.validation.constraints.*;

public record RegisterRequest(
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 128, message = "Senha deve ter entre 8 e 128 caracteres")
    String password,

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
    String name,

    @NotBlank(message = "Nome da empresa é obrigatório")
    @Size(min = 2, max = 100, message = "Nome da empresa deve ter entre 2 e 100 caracteres")
    String companyName,

    @NotBlank(message = "Slug da empresa é obrigatório")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug inválido")
    @Size(min = 2, max = 50)
    String companySlug
) {}
