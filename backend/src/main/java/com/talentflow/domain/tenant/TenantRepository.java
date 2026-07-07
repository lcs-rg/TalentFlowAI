package com.talentflow.domain.tenant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Tenant aggregate.
 * Domain defines the contract — infrastructure implements it.
 */
public interface TenantRepository {
    Tenant save(Tenant tenant);
    Optional<Tenant> findById(UUID id);
    Optional<Tenant> findBySlug(String slug);
    List<Tenant> findAll();
    boolean existsBySlug(String slug);
    void delete(Tenant tenant);
}
