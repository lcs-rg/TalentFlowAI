package com.talentflow.domain.notification;

import java.time.Instant;
import java.util.UUID;

public class Notification {
    private final UUID id;
    private final UUID companyId;
    private final UUID userId;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private String resourceType;
    private UUID resourceId;
    private final Instant createdAt;

    public Notification(UUID id, UUID companyId, UUID userId, String title, String type) {
        this.id = id;
        this.companyId = companyId;
        this.userId = userId;
        this.title = title;
        this.type = type != null ? type : "INFO";
        this.read = false;
        this.createdAt = Instant.now();
    }

    public void markRead() { this.read = true; }

    public UUID getId() { return id; }
    public UUID getCompanyId() { return companyId; }
    public UUID getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public boolean isRead() { return read; }
    public String getResourceType() { return resourceType; }
    public UUID getResourceId() { return resourceId; }
    public Instant getCreatedAt() { return createdAt; }

    public void setMessage(String message) { this.message = message; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public void setResourceId(UUID resourceId) { this.resourceId = resourceId; }
}
