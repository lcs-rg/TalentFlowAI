package com.talentflow.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TenantJpaRepository extends JpaRepository<TenantJpaEntity, UUID> {
    Optional<TenantJpaEntity> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
