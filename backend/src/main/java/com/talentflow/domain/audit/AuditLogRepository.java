package com.talentflow.domain.audit;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findByTenant(UUID tenantId, int limit);
    List<AuditLog> findByResource(UUID tenantId, String resourceType, UUID resourceId);
    List<AuditLog> findByUser(UUID userId, int limit);
}
