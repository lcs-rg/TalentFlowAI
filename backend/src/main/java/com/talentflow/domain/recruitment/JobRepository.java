package com.talentflow.domain.recruitment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository {
    Job save(Job job);
    Optional<Job> findById(UUID id);
    List<Job> findByCompany(UUID companyId);
    Page<Job> findByTenantWithFilters(UUID companyId, String status, String search, String type, Pageable pageable);
    List<Job> findByCompanyAndStatus(UUID companyId, JobStatus status);
    long countByCompany(UUID companyId);
    long countByCompanyAndStatus(UUID companyId, JobStatus status);
    void softDelete(UUID jobId, UUID deletedBy, Instant deletedAt);
    void delete(Job job);
}
