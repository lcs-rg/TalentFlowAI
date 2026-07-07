package com.talentflow.domain.company;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyUserRepository {
    CompanyUser save(CompanyUser cu);
    Optional<CompanyUser> findById(UUID id);
    Optional<CompanyUser> findByCompanyAndUser(UUID companyId, UUID userId);
    List<CompanyUser> findByCompany(UUID companyId);
    List<CompanyUser> findByTeam(UUID teamId);
}
