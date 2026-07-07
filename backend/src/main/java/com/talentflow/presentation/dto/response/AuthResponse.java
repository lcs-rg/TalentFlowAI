package com.talentflow.presentation.dto.response;

import java.util.UUID;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    int expiresIn,
    UserResponse user
) {}
