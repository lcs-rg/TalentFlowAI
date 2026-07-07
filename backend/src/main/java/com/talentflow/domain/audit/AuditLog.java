package com.talentflow.domain.audit;

import com.talentflow.domain.shared.BaseEntity;
import com.talentflow.domain.shared.TenantAware;

import java.time.Instant;
import java.util.*;

/**
 * AuditLog — immutable record of every state-changing action in the system.
 */
public class AuditLog extends BaseEntity implements TenantAware {

    private final UUID tenantId;
    private final UUID userId;
    private final String action;
    private final String resourceType;
    private final UUID resourceId;
    private final Map<String, Object> oldValue;
    private final Map<String, Object> newValue;
    private final String ipAddress;
    private final String userAgent;
    private final Instant createdAt;

    public AuditLog(UUID id, UUID tenantId, UUID userId, String action, String resourceType,
                    UUID resourceId, Map<String, Object> oldValue, Map<String, Object> newValue,
                    String ipAddress, String userAgent) {
        super(id);
        this.tenantId = tenantId;
        this.userId = userId;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.oldValue = oldValue != null ? new HashMap<>(oldValue) : new HashMap<>();
        this.newValue = newValue != null ? new HashMap<>(newValue) : new HashMap<>();
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = Instant.now();
    }

    @Override public UUID getTenantId() { return tenantId; }
    public UUID getUserId() { return userId; }
    public String getAction() { return action; }
    public String getResourceType() { return resourceType; }
    public UUID getResourceId() { return resourceId; }
    public Map<String, Object> getOldValue() { return Collections.unmodifiableMap(oldValue); }
    public Map<String, Object> getNewValue() { return Collections.unmodifiableMap(newValue); }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public Instant getCreatedAt() { return createdAt; }
}
