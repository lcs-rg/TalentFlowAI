package com.talentflow.infrastructure.persistence;

import com.talentflow.domain.team.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.UUID;

@Entity @Table(name = "teams")
class TeamJpaEntity {
    @Id @Column(name = "id") UUID id;
    @Column(name = "company_id") UUID companyId;
    @Column(name = "name") String name;
    public UUID getId() { return id; }
    public UUID getCompanyId() { return companyId; }
    public String getName() { return name; }
}

interface TeamJpaRepository extends JpaRepository<TeamJpaEntity, UUID> {
    List<TeamJpaEntity> findByCompanyId(UUID companyId);
}

@Repository
class TeamRepositoryImpl implements TeamRepository {
    private final TeamJpaRepository jpa;
    TeamRepositoryImpl(TeamJpaRepository jpa) { this.jpa = jpa; }

    @Override public Team save(Team t) { return toDomain(jpa.save(toEntity(t))); }
    @Override public Optional<Team> findById(UUID id) { return jpa.findById(id).map(this::toDomain); }
    @Override public List<Team> findByCompany(UUID companyId) { return jpa.findByCompanyId(companyId).stream().map(this::toDomain).toList(); }
    @Override public Optional<Team> findDefaultByCompany(UUID companyId) { return jpa.findByCompanyId(companyId).stream().findFirst().map(this::toDomain); }

    private TeamJpaEntity toEntity(Team t) { var e = new TeamJpaEntity(); e.id = t.getId(); e.companyId = t.getCompanyId(); e.name = t.getName(); return e; }
    private Team toDomain(TeamJpaEntity e) { return new Team(e.getId(), e.getCompanyId(), e.getName()); }
}
