package com.talentflow.domain.team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository {
    Team save(Team team);
    Optional<Team> findById(UUID id);
    List<Team> findByCompany(UUID companyId);
    Optional<Team> findDefaultByCompany(UUID companyId);
}
