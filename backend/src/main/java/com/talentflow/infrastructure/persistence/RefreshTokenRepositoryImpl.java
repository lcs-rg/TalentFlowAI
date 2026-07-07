package com.talentflow.infrastructure.persistence;

import com.talentflow.domain.auth.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Entity @Table(name = "refresh_tokens")
class RefreshTokenJpaEntity {
    @Id @Column(name = "id") UUID id;
    @Column(name = "user_id") UUID userId;
    @Column(name = "token_hash") String tokenHash;
    @Column(name = "device_id") String deviceId;
    @Column(name = "device_info") String deviceInfo;
    @Column(name = "used") boolean used;
    @Column(name = "expires_at") Instant expiresAt;
    @Column(name = "created_at") Instant createdAt;

    public UUID getId() { return id; } public void setId(UUID v) { this.id = v; }
    public UUID getUserId() { return userId; } public void setUserId(UUID v) { this.userId = v; }
    public String getTokenHash() { return tokenHash; } public void setTokenHash(String v) { this.tokenHash = v; }
    public String getDeviceId() { return deviceId; } public void setDeviceId(String v) { this.deviceId = v; }
    public String getDeviceInfo() { return deviceInfo; } public void setDeviceInfo(String v) { this.deviceInfo = v; }
    public boolean isUsed() { return used; } public void setUsed(boolean v) { this.used = v; }
    public Instant getExpiresAt() { return expiresAt; } public void setExpiresAt(Instant v) { this.expiresAt = v; }
    public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant v) { this.createdAt = v; }
}

interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {
    Optional<RefreshTokenJpaEntity> findByTokenHash(String hash);
    List<RefreshTokenJpaEntity> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
    void deleteByUserIdAndDeviceId(UUID userId, String deviceId);
}

@Repository
class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository jpa;
    RefreshTokenRepositoryImpl(RefreshTokenJpaRepository jpa) { this.jpa = jpa; }

    @Override public RefreshToken save(RefreshToken rt) {
        RefreshTokenJpaEntity e = new RefreshTokenJpaEntity();
        e.setId(rt.getId()); e.setUserId(rt.getUserId());
        e.setTokenHash(rt.getTokenHash()); e.setDeviceId(rt.getDeviceId());
        e.setDeviceInfo(rt.getDeviceInfo()); e.setUsed(rt.isUsed());
        e.setExpiresAt(rt.getExpiresAt()); e.setCreatedAt(rt.getCreatedAt());
        jpa.save(e); return rt;
    }

    @Override public Optional<RefreshToken> findByHash(String hash) {
        return jpa.findByTokenHash(hash).map(this::toDomain);
    }

    @Override public List<RefreshToken> findByUser(UUID userId) {
        return jpa.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override public void deleteByUser(UUID userId) { jpa.deleteByUserId(userId); }

    @Override public void deleteByUserAndDevice(UUID userId, String deviceId) {
        jpa.deleteByUserIdAndDeviceId(userId, deviceId);
    }

    private RefreshToken toDomain(RefreshTokenJpaEntity e) {
        return RefreshToken.reconstitute(e.getId(), e.getUserId(), e.getTokenHash(),
                e.getDeviceId(), e.getDeviceInfo(), e.isUsed(), e.getExpiresAt(), e.getCreatedAt());
    }
}
