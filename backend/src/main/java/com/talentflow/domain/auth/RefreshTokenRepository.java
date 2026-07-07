package com.talentflow.domain.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findByHash(String tokenHash);
    List<RefreshToken> findByUser(UUID userId);
    void deleteByUser(UUID userId);
    void deleteByUserAndDevice(UUID userId, String deviceId);
}
