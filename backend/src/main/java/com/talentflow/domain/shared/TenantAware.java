package com.talentflow.domain.shared;

import java.util.UUID;

/**
 * Marker interface for entities that belong to a tenant.
 * Every query MUST filter by tenant for multi-tenant isolation.
 */
public interface TenantAware {
    UUID getTenantId();
}
