package com.talentflow.domain.recruitment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewRepository {
    Interview save(Interview interview);
    Optional<Interview> findById(UUID id);
    List<Interview> findByCandidate(UUID candidateId);
    void delete(Interview interview);
}
