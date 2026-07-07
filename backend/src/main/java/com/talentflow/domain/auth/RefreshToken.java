package com.talentflow.domain.auth;

import com.talentflow.domain.shared.BaseEntity;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * Opaque refresh token — not a JWT.
 * Persisted in DB, supports device-level logout and reuse detection.
 */
public class RefreshToken extends BaseEntity {
    private final UUID userId;
    private final String tokenHash;
    private final String deviceId;
    private final String deviceInfo;
    private boolean used;
    private final Instant expiresAt;

    private RefreshToken(UUID id, UUID userId, String tokenHash, String deviceId, String deviceInfo, Instant expiresAt) {
        super(id);
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
        this.used = false;
        this.expiresAt = expiresAt;
    }

    public static RefreshToken create(UUID userId, String deviceId, String deviceInfo, long ttlMillis) {
        String rawToken = generateRawToken();
        String hash = sha256(rawToken);
        return new RefreshToken(UUID.randomUUID(), userId, hash, deviceId, deviceInfo, Instant.now().plusMillis(ttlMillis));
    }

    public void markUsed() {
        if (this.used) throw new IllegalStateException("Refresh token already used — possible compromise");
        this.used = true;
        markUpdated();
    }

    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
    public boolean isUsed() { return used; }
    public UUID getUserId() { return userId; }
    public String getTokenHash() { return tokenHash; }
    public String getDeviceId() { return deviceId; }
    public String getDeviceInfo() { return deviceInfo; }
    public Instant getExpiresAt() { return expiresAt; }

    private static String generateRawToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    /** Reconstitute from DB — for JPA mapping only. */
    public static RefreshToken reconstitute(UUID id, UUID userId, String tokenHash, String deviceId, String deviceInfo, boolean used, Instant expiresAt, Instant createdAt) {
        return new RefreshToken(id, userId, tokenHash, deviceId, deviceInfo, used, expiresAt, createdAt);
    }

    private RefreshToken(UUID id, UUID userId, String tokenHash, String deviceId, String deviceInfo, boolean used, Instant expiresAt, Instant createdAt) {
        super(id);
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
        this.used = used;
        this.expiresAt = expiresAt;
    }
}
