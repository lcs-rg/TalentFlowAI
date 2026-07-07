package com.talentflow.domain.recruitment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

/**
 * Candidate is a platform-level entity. Not scoped to a tenant.
 */
public interface CandidateRepository {
    Candidate save(Candidate candidate);
    Optional<Candidate> findById(UUID id);
    Page<Candidate> search(String search, Pageable pageable);
    Optional<Candidate> findByEmail(String email);
    Optional<Candidate> findByEmailWithPassword(String email);
    void softDelete(UUID id, UUID deletedBy, java.time.Instant deletedAt);
}
