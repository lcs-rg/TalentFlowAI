package com.talentflow.presentation.dto.response;

import java.util.UUID;

public record UserResponse(
    UUID id,
    UUID tenantId,
    String email,
    String name,
    String role,
    String avatarUrl
) {}
