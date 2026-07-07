package com.talentflow.domain.shared;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all domain entities.
 * Pure domain — no JPA annotations, no framework dependencies.
 */
public abstract class BaseEntity {

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    protected BaseEntity(UUID id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    protected BaseEntity() {
        this(UUID.randomUUID());
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    protected void markUpdated() {
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
