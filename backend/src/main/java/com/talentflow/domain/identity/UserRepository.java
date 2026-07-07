package com.talentflow.domain.identity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for User aggregate.
 */
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(UUID tenantId, String email);
    boolean existsByEmail(UUID tenantId, String email);
    List<User> findByEmailAcrossTenants(String email);
}
