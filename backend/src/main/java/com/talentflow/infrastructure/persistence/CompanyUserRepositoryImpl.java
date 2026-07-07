package com.talentflow.infrastructure.persistence;

import com.talentflow.domain.company.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.UUID;

@Entity @Table(name = "company_users")
class CompanyUserJpaEntity {
    @Id @Column(name = "id") UUID id;
    @Column(name = "company_id") UUID companyId;
    @Column(name = "user_id") UUID userId;
    @Column(name = "team_id") UUID teamId;
    @Column(name = "role") String role;
    public UUID getId() { return id; }
    public UUID getCompanyId() { return companyId; }
    public UUID getUserId() { return userId; }
    public UUID getTeamId() { return teamId; }
    public String getRole() { return role; }
}

interface CompanyUserJpaRepository extends JpaRepository<CompanyUserJpaEntity, UUID> {
    Optional<CompanyUserJpaEntity> findByCompanyIdAndUserId(UUID companyId, UUID userId);
    List<CompanyUserJpaEntity> findByCompanyId(UUID companyId);
    List<CompanyUserJpaEntity> findByTeamId(UUID teamId);
}

@Repository
class CompanyUserRepositoryImpl implements CompanyUserRepository {
    private final CompanyUserJpaRepository jpa;
    CompanyUserRepositoryImpl(CompanyUserJpaRepository jpa) { this.jpa = jpa; }

    @Override public CompanyUser save(CompanyUser cu) { return toDomain(jpa.save(toEntity(cu))); }
    @Override public Optional<CompanyUser> findById(UUID id) { return jpa.findById(id).map(this::toDomain); }
    @Override public Optional<CompanyUser> findByCompanyAndUser(UUID companyId, UUID userId) { return jpa.findByCompanyIdAndUserId(companyId, userId).map(this::toDomain); }
    @Override public List<CompanyUser> findByCompany(UUID companyId) { return jpa.findByCompanyId(companyId).stream().map(this::toDomain).toList(); }
    @Override public List<CompanyUser> findByTeam(UUID teamId) { return jpa.findByTeamId(teamId).stream().map(this::toDomain).toList(); }

    private CompanyUserJpaEntity toEntity(CompanyUser cu) { var e = new CompanyUserJpaEntity(); e.id = cu.getId(); e.companyId = cu.getCompanyId(); e.userId = cu.getUserId(); e.teamId = cu.getTeamId(); e.role = cu.getRole(); return e; }
    private CompanyUser toDomain(CompanyUserJpaEntity e) { return new CompanyUser(e.getId(), e.getCompanyId(), e.getUserId(), e.getTeamId(), e.getRole()); }
}
