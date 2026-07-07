package com.talentflow.infrastructure.persistence;

import com.talentflow.domain.tenant.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * TenantRepository implementation — converts between domain Tenant and JPA entity.
 * Infrastructure layer. Domain defines the contract. This fulfills it.
 */
@Repository
public class TenantRepositoryImpl implements TenantRepository {

    private final TenantJpaRepository jpaRepository;

    public TenantRepositoryImpl(TenantJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Tenant save(Tenant tenant) {
        TenantJpaEntity entity = toJpaEntity(tenant);
        TenantJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Tenant> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Tenant> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toDomain);
    }

    @Override
    public List<Tenant> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    public void delete(Tenant tenant) {
        jpaRepository.deleteById(tenant.getId());
    }

    // --- Mapping ---

    private Tenant toDomain(TenantJpaEntity entity) {
        return new Tenant(
            entity.getId(),
            entity.getSlug(),
            entity.getName(),
            TenantPlan.valueOf(entity.getPlan())
        );
    }

    private TenantJpaEntity toJpaEntity(Tenant tenant) {
        return new TenantJpaEntity(
            tenant.getId(),
            tenant.getSlug(),
            tenant.getName(),
            tenant.getPlan().name(),
            tenant.getStatus().name(),
            "{}",
            tenant.getCreatedAt(),
            tenant.getUpdatedAt()
        );
    }
}
