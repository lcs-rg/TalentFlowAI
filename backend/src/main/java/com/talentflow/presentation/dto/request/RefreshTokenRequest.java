package com.talentflow.presentation.dto.request;
import jakarta.validation.constraints.*;
public record RefreshTokenRequest(@NotBlank(message = "Refresh token é obrigatório") String refreshToken) {}