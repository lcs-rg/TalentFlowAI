package com.talentflow.infrastructure.persistence;

import com.talentflow.domain.identity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    @Query("SELECT u FROM UserJpaEntity u WHERE u.tenantId = :tenantId AND u.email = :email")
    Optional<UserJpaEntity> findByTenantIdAndEmail(UUID tenantId, String email);

    @Query("SELECT COUNT(u) > 0 FROM UserJpaEntity u WHERE u.tenantId = :tenantId AND u.email = :email")
    boolean existsByTenantIdAndEmail(UUID tenantId, String email);
}

@Repository
class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    UserRepositoryImpl(UserJpaRepository jpaRepository) { this.jpaRepository = jpaRepository; }

    @Override
    public User save(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setTenantId(user.getTenantId());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setName(user.getName());
        entity.setRole(user.getRole().name());
        entity.setAvatarUrl(user.getAvatarUrl());
        entity.setEnabled(user.isEnabled());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        jpaRepository.save(entity);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(UUID tenantId, String email) {
        return jpaRepository.findByTenantIdAndEmail(tenantId, email.toLowerCase()).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(UUID tenantId, String email) {
        return jpaRepository.existsByTenantIdAndEmail(tenantId, email.toLowerCase());
    }

    private User toDomain(UserJpaEntity e) {
        return new User(e.getId(), e.getTenantId(), e.getEmail(), e.getPasswordHash(),
                       e.getName(), Role.valueOf(e.getRole()));
    }
}
