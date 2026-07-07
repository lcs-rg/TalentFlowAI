package com.talentflow.domain.recruitment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository {
    Application save(Application app);
    Optional<Application> findById(UUID id);
    Page<Application> findByJob(UUID jobId, Pageable pageable);
    Page<Application> findByCandidate(UUID candidateId, Pageable pageable);
    Optional<Application> findByJobAndCandidate(UUID jobId, UUID candidateId);
}
