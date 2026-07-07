package com.talentflow.domain.company;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {
    Company save(Company company);
    Optional<Company> findById(UUID id);
    Optional<Company> findByTenantId(UUID tenantId);
}
