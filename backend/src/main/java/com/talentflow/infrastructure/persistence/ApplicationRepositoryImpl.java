package com.talentflow.infrastructure.persistence;

import com.talentflow.domain.recruitment.*;
import jakarta.persistence.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity @Table(name = "applications")
class ApplicationJpaEntity {
    @Id @Column(name = "id") UUID id;
    @Column(name = "candidate_id") UUID candidateId;
    @Column(name = "job_id") UUID jobId;
    @Column(name = "company_id") UUID companyId;
    @Column(name = "team_id") UUID teamId;
    @Column(name = "stage_id") UUID stageId;
    @Column(name = "status") String status;
    @Column(name = "score") Float score;

    // Getters
    public UUID getId() { return id; }
    public UUID getCandidateId() { return candidateId; }
    public UUID getJobId() { return jobId; }
    public UUID getCompanyId() { return companyId; }
    public UUID getTeamId() { return teamId; }
    public UUID getStageId() { return stageId; }
    public String getStatus() { return status; }
    public Float getScore() { return score; }
}

interface ApplicationJpaRepository extends JpaRepository<ApplicationJpaEntity, UUID> {
    List<ApplicationJpaEntity> findByJobId(UUID jobId);
    List<ApplicationJpaEntity> findByCandidateId(UUID candidateId);
    Optional<ApplicationJpaEntity> findByJobIdAndCandidateId(UUID jobId, UUID candidateId);
}

@Repository
class ApplicationRepositoryImpl implements ApplicationRepository {

    private final ApplicationJpaRepository jpa;

    ApplicationRepositoryImpl(ApplicationJpaRepository jpa) { this.jpa = jpa; }

    @Override public Application save(Application app) {
        return toDomain(jpa.save(toEntity(app)));
    }

    @Override public Optional<Application> findById(UUID id) { return jpa.findById(id).map(this::toDomain); }

    @Override public Page<Application> findByJob(UUID jobId, Pageable pageable) {
        var list = jpa.findByJobId(jobId);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end).stream().map(this::toDomain).toList(),
                pageable, list.size());
    }

    @Override public Page<Application> findByCandidate(UUID candidateId, Pageable pageable) {
        var list = jpa.findByCandidateId(candidateId);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end).stream().map(this::toDomain).toList(),
                pageable, list.size());
    }

    @Override public Optional<Application> findByJobAndCandidate(UUID jobId, UUID candidateId) {
        return jpa.findByJobIdAndCandidateId(jobId, candidateId).map(this::toDomain);
    }

    private ApplicationJpaEntity toEntity(Application a) {
        var e = new ApplicationJpaEntity();
        e.id = a.getId(); e.candidateId = a.getCandidateId(); e.jobId = a.getJobId();
        e.companyId = a.getCompanyId(); e.teamId = a.getTeamId(); e.stageId = a.getStageId();
        e.status = a.getStatus().name(); e.score = a.getScore();
        return e;
    }

    private Application toDomain(ApplicationJpaEntity e) {
        Application a = new Application(e.getId(), e.getCompanyId(), e.getTeamId(), e.getJobId(), e.getCandidateId());
        a.updateStatus(ApplicationStatus.valueOf(e.getStatus()));
        if (e.getScore() != null) a.setScore(e.getScore());
        if (e.getStageId() != null) a.moveToStage(e.getStageId());
        return a;
    }
}
