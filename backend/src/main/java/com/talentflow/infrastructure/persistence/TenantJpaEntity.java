package com.talentflow.infrastructure.persistence;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity for Tenant. Maps domain Tenant to database.
 * Infrastructure layer — domain knows nothing about this.
 */
@Entity
@Table(name = "tenants")
public class TenantJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "plan", nullable = false)
    private String plan;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "settings")
    private String settings;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public TenantJpaEntity() {}

    public TenantJpaEntity(UUID id, String slug, String name, String plan, String status,
                           String settings, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.plan = plan;
        this.status = status;
        this.settings = settings;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSettings() { return settings; }
    public void setSettings(String settings) { this.settings = settings; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
