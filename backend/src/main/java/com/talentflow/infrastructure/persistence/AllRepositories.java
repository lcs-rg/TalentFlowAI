package com.talentflow.infrastructure.persistence;

import com.talentflow.domain.company.*;
import com.talentflow.domain.recruitment.*;
import com.talentflow.domain.audit.*;
import jakarta.persistence.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

// ─── Company ─────────────────────────────────────────────
@Entity @Table(name = "companies")
class CompanyJpaEntity {
    @Id @Column(name = "id") UUID id;
    @Column(name = "tenant_id") UUID tenantId;
    @Column(name = "name") String name;
    @Column(name = "logo_url") String logoUrl;
    @Column(name = "industry") String industry;
    @Column(name = "size") String size;
    @Column(name = "website") String website;
    @Column(name = "settings", columnDefinition = "jsonb") String settings;
    @Column(name = "created_at") Instant createdAt;
    @Column(name = "updated_at") Instant updatedAt;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getTenantId() { return tenantId; } public void setTenantId(UUID v) { this.tenantId = v; }
    public String getName() { return name; } public void setName(String v) { this.name = v; }
    public String getLogoUrl() { return logoUrl; } public void setLogoUrl(String v) { this.logoUrl = v; }
    public String getIndustry() { return industry; } public void setIndustry(String v) { this.industry = v; }
    public String getSize() { return size; } public void setSize(String v) { this.size = v; }
    public String getWebsite() { return website; } public void setWebsite(String v) { this.website = v; }
    public String getSettings() { return settings; } public void setSettings(String v) { this.settings = v; }
    public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant v) { this.createdAt = v; }
    public Instant getUpdatedAt() { return updatedAt; } public void setUpdatedAt(Instant v) { this.updatedAt = v; }
}

interface CompanyJpaRepository extends JpaRepository<CompanyJpaEntity, UUID> {
    Optional<CompanyJpaEntity> findByTenantId(UUID tenantId);
}

@Repository
class CompanyRepositoryImpl implements CompanyRepository {
    private final CompanyJpaRepository jpa;
    CompanyRepositoryImpl(CompanyJpaRepository jpa) { this.jpa = jpa; }

    @Override public Company save(Company c) {
        CompanyJpaEntity e = new CompanyJpaEntity();
        e.setId(c.getId()); e.setTenantId(c.getTenantId()); e.setName(c.getName());
        e.setLogoUrl(c.getLogoUrl()); e.setIndustry(c.getIndustry());
        e.setSize(c.getSize() != null ? c.getSize().name() : null);
        e.setWebsite(c.getWebsite()); e.setSettings("{}");
        e.setCreatedAt(c.getCreatedAt()); e.setUpdatedAt(c.getUpdatedAt());
        jpa.save(e); return c;
    }

    @Override public Optional<Company> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override public Optional<Company> findByTenantId(UUID tenantId) {
        return jpa.findByTenantId(tenantId).map(this::toDomain);
    }

    private Company toDomain(CompanyJpaEntity e) {
        return new Company(e.getId(), e.getTenantId(), e.getName());
    }
}

// ─── PipelineStage ───────────────────────────────────────
@Entity @Table(name = "pipeline_stages")
class PipelineStageJpaEntity {
    @Id @Column(name = "id") UUID id;
    @Column(name = "tenant_id") UUID tenantId;
    @Column(name = "job_id") UUID jobId;
    @Column(name = "name") String name;
    @Column(name = "order_index") int orderIndex;
    @Column(name = "type") String type;
    @Column(name = "color") String color;
    @Column(name = "created_at") Instant createdAt;
    @Column(name = "deleted_at") Instant deletedAt;

    public UUID getId() { return id; } public void setId(UUID v) { this.id = v; }
    public UUID getTenantId() { return tenantId; } public void setTenantId(UUID v) { this.tenantId = v; }
    public UUID getJobId() { return jobId; } public void setJobId(UUID v) { this.jobId = v; }
    public String getName() { return name; } public void setName(String v) { this.name = v; }
    public int getOrderIndex() { return orderIndex; } public void setOrderIndex(int v) { this.orderIndex = v; }
    public String getType() { return type; } public void setType(String v) { this.type = v; }
    public String getColor() { return color; } public void setColor(String v) { this.color = v; }
    public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant v) { this.createdAt = v; }
}

interface PipelineStageJpaRepository extends JpaRepository<PipelineStageJpaEntity, UUID> {
    List<PipelineStageJpaEntity> findByJobIdOrderByOrderIndexAsc(UUID jobId);
    void deleteByJobId(UUID jobId);
}

@Repository
class PipelineStageRepositoryImpl implements PipelineStageRepository {
    private final PipelineStageJpaRepository jpa;
    PipelineStageRepositoryImpl(PipelineStageJpaRepository jpa) { this.jpa = jpa; }

    @Override public PipelineStage save(PipelineStage s) {
        PipelineStageJpaEntity e = new PipelineStageJpaEntity();
        e.setId(s.getId()); e.setTenantId(s.getTenantId()); e.setJobId(s.getJobId());
        e.setName(s.getName()); e.setOrderIndex(s.getOrderIndex());
        e.setType(s.getType().name()); e.setColor(s.getColor()); e.setCreatedAt(s.getCreatedAt());
        jpa.save(e); return s;
    }

    @Override public Optional<PipelineStage> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override public List<PipelineStage> findByJob(UUID jobId) {
        return findByJobOrdered(jobId);
    }

    @Override public List<PipelineStage> findByJobOrdered(UUID jobId) {
        return jpa.findByJobIdOrderByOrderIndexAsc(jobId).stream().map(this::toDomain).toList();
    }

    @Override public void delete(PipelineStage s) { jpa.deleteById(s.getId()); }
    @Override public void deleteByJob(UUID jobId) { jpa.deleteByJobId(jobId); }

    private PipelineStage toDomain(PipelineStageJpaEntity e) {
        return new PipelineStage(e.getId(), e.getTenantId(), e.getJobId(),
                                 e.getName(), e.getOrderIndex(), StageType.valueOf(e.getType()));
    }
}

// ─── Candidate (Platform-level) ──────────────────────
@Entity @Table(name = "candidates")
class CandidateJpaEntity {
    @Id @Column(name = "id") UUID id;
    @Column(name = "name") String name;
    @Column(name = "email") String email;
    @Column(name = "phone") String phone;
    @Column(name = "resume_url") String resumeUrl;
    @Column(name = "resume_text", columnDefinition = "TEXT") String resumeText;
    @Column(name = "tags", columnDefinition = "jsonb") String tags;
    @Column(name = "notes", columnDefinition = "TEXT") String notes;
    @Column(name = "created_at") Instant createdAt;
    @Column(name = "updated_at") Instant updatedAt;
    @Column(name = "deleted_at") Instant deletedAt;
    @Column(name = "deleted_by") UUID deletedBy;
    @Column(name = "password_hash") String passwordHash;
    @Column(name = "status") String status = "NEW";

    public UUID getId() { return id; } public void setId(UUID v) { this.id = v; }
    public String getName() { return name; } public void setName(String v) { this.name = v; }
    public String getEmail() { return email; } public void setEmail(String v) { this.email = v; }
    public String getPhone() { return phone; } public void setPhone(String v) { this.phone = v; }
    public String getResumeUrl() { return resumeUrl; } public void setResumeUrl(String v) { this.resumeUrl = v; }
    public String getResumeText() { return resumeText; } public void setResumeText(String v) { this.resumeText = v; }
    public String getTags() { return tags; } public void setTags(String v) { this.tags = v; }
    public String getNotes() { return notes; } public void setNotes(String v) { this.notes = v; }
    public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant v) { this.createdAt = v; }
    public Instant getUpdatedAt() { return updatedAt; } public void setUpdatedAt(Instant v) { this.updatedAt = v; }
    public Instant getDeletedAt() { return deletedAt; } public void setDeletedAt(Instant v) { this.deletedAt = v; }
    public UUID getDeletedBy() { return deletedBy; } public void setDeletedBy(UUID v) { this.deletedBy = v; }
    public String getPasswordHash() { return passwordHash; } public void setPasswordHash(String v) { this.passwordHash = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
}

interface CandidateJpaRepository extends JpaRepository<CandidateJpaEntity, UUID> {
    @Query("SELECT c FROM CandidateJpaEntity c WHERE c.deletedAt IS NULL " +
           "AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY c.createdAt DESC")
    Page<CandidateJpaEntity> search(String search, Pageable pageable);

    Optional<CandidateJpaEntity> findByEmailIgnoreCase(String email);
}

@Repository
class CandidateRepositoryImpl implements CandidateRepository {
    private final CandidateJpaRepository jpa;
    CandidateRepositoryImpl(CandidateJpaRepository jpa) { this.jpa = jpa; }

    @Override public Candidate save(Candidate c) {
        CandidateJpaEntity e = jpa.findById(c.getId()).orElse(new CandidateJpaEntity());
        e.setId(c.getId()); e.setName(c.getName());
        e.setEmail(c.getEmail()); e.setPhone(c.getPhone());
        e.setResumeUrl(c.getResumeUrl()); e.setResumeText(c.getResumeText());
        e.setTags(null); e.setNotes(c.getNotes());
        e.setCreatedAt(c.getCreatedAt()); e.setUpdatedAt(c.getUpdatedAt());
        if (c.getPasswordHash() != null) e.setPasswordHash(c.getPasswordHash());
        jpa.save(e); return c;
    }

    @Override public Optional<Candidate> findById(UUID id) {
        return jpa.findById(id).map(e -> new Candidate(e.getId(), e.getName(), e.getEmail()));
    }

    @Override
    public Page<Candidate> search(String search, Pageable pageable) {
        return jpa.search(search, pageable).map(e -> new Candidate(e.getId(), e.getName(), e.getEmail()));
    }

    @Override
    public Optional<Candidate> findByEmail(String email) {
        return jpa.findByEmailIgnoreCase(email).map(e -> new Candidate(e.getId(), e.getName(), e.getEmail()));
    }

    @Override
    public Optional<Candidate> findByEmailWithPassword(String email) {
        return jpa.findByEmailIgnoreCase(email).map(e -> {
            Candidate c = new Candidate(e.getId(), e.getName(), e.getEmail());
            if (e.getPasswordHash() != null) c.setPassword(e.getPasswordHash());
            return c;
        });
    }

    @Override
    public void softDelete(UUID id, UUID deletedBy, java.time.Instant deletedAt) {
        jpa.findById(id).ifPresent(e -> {
            e.setDeletedAt(deletedAt);
            e.setDeletedBy(deletedBy);
            jpa.save(e);
        });
    }
}

// ─── Interview ───────────────────────────────────────────
@Entity @Table(name = "interviews")
class InterviewJpaEntity {
    @Id @Column(name = "id") UUID id;
    @Column(name = "candidate_id") UUID candidateId;
    @Column(name = "scheduled_at") Instant scheduledAt;
    @Column(name = "type") String type;
    @Column(name = "status") String status;
    @Column(name = "ai_questions", columnDefinition = "jsonb") String aiQuestions;
    @Column(name = "feedback", columnDefinition = "jsonb") String feedback;
    @Column(name = "recording_url") String recordingUrl;
    @Column(name = "created_at") Instant createdAt;
    @Column(name = "updated_at") Instant updatedAt;

    public UUID getId() { return id; } public void setId(UUID v) { this.id = v; }
    public UUID getCandidateId() { return candidateId; } public void setCandidateId(UUID v) { this.candidateId = v; }
    public Instant getScheduledAt() { return scheduledAt; } public void setScheduledAt(Instant v) { this.scheduledAt = v; }
    public String getType() { return type; } public void setType(String v) { this.type = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public String getAiQuestions() { return aiQuestions; } public void setAiQuestions(String v) { this.aiQuestions = v; }
    public String getFeedback() { return feedback; } public void setFeedback(String v) { this.feedback = v; }
    public String getRecordingUrl() { return recordingUrl; } public void setRecordingUrl(String v) { this.recordingUrl = v; }
    public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant v) { this.createdAt = v; }
    public Instant getUpdatedAt() { return updatedAt; } public void setUpdatedAt(Instant v) { this.updatedAt = v; }
}

interface InterviewJpaRepository extends JpaRepository<InterviewJpaEntity, UUID> {
    List<InterviewJpaEntity> findByCandidateId(UUID candidateId);
}

@Repository
class InterviewRepositoryImpl implements InterviewRepository {
    private final InterviewJpaRepository jpa;
    InterviewRepositoryImpl(InterviewJpaRepository jpa) { this.jpa = jpa; }

    @Override public Interview save(Interview i) {
        InterviewJpaEntity e = new InterviewJpaEntity();
        e.setId(i.getId()); e.setCandidateId(i.getCandidateId());
        e.setScheduledAt(i.getScheduledAt()); e.setType(i.getType().name());
        e.setStatus(i.getStatus().name()); e.setAiQuestions("[]"); e.setFeedback("{}");
        e.setCreatedAt(i.getCreatedAt()); e.setUpdatedAt(i.getUpdatedAt());
        jpa.save(e); return i;
    }

    @Override public Optional<Interview> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override public List<Interview> findByCandidate(UUID candidateId) {
        return jpa.findByCandidateId(candidateId).stream().map(this::toDomain).toList();
    }

    @Override public void delete(Interview i) { jpa.deleteById(i.getId()); }

    private Interview toDomain(InterviewJpaEntity e) {
        return new Interview(e.getId(), e.getCandidateId(), InterviewType.valueOf(e.getType()));
    }
}

// ─── AuditLog ────────────────────────────────────────────
@Entity @Table(name = "audit_logs")
class AuditLogJpaEntity {
    @Id @Column(name = "id") UUID id;
    @Column(name = "tenant_id") UUID tenantId;
    @Column(name = "user_id") UUID userId;
    @Column(name = "action") String action;
    @Column(name = "resource_type") String resourceType;
    @Column(name = "resource_id") UUID resourceId;
    @Column(name = "old_value", columnDefinition = "jsonb") String oldValue;
    @Column(name = "new_value", columnDefinition = "jsonb") String newValue;
    @Column(name = "ip_address") String ipAddress;
    @Column(name = "user_agent") String userAgent;
    @Column(name = "created_at") Instant createdAt;

    public UUID getId() { return id; } public void setId(UUID v) { this.id = v; }
    public UUID getTenantId() { return tenantId; } public void setTenantId(UUID v) { this.tenantId = v; }
    public UUID getUserId() { return userId; } public void setUserId(UUID v) { this.userId = v; }
    public String getAction() { return action; } public void setAction(String v) { this.action = v; }
    public String getResourceType() { return resourceType; } public void setResourceType(String v) { this.resourceType = v; }
    public UUID getResourceId() { return resourceId; } public void setResourceId(UUID v) { this.resourceId = v; }
    public String getOldValue() { return oldValue; } public void setOldValue(String v) { this.oldValue = v; }
    public String getNewValue() { return newValue; } public void setNewValue(String v) { this.newValue = v; }
    public String getIpAddress() { return ipAddress; } public void setIpAddress(String v) { this.ipAddress = v; }
    public String getUserAgent() { return userAgent; } public void setUserAgent(String v) { this.userAgent = v; }
    public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant v) { this.createdAt = v; }
}

interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, UUID> {
    List<AuditLogJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId, org.springframework.data.domain.Pageable pageable);
    List<AuditLogJpaEntity> findByTenantIdAndResourceTypeAndResourceIdOrderByCreatedAtDesc(UUID tenantId, String resourceType, UUID resourceId);
    List<AuditLogJpaEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, org.springframework.data.domain.Pageable pageable);
}

@Repository
class AuditLogRepositoryImpl implements AuditLogRepository {
    private final AuditLogJpaRepository jpa;
    AuditLogRepositoryImpl(AuditLogJpaRepository jpa) { this.jpa = jpa; }

    @Override
    public AuditLog save(AuditLog log) {
        AuditLogJpaEntity e = new AuditLogJpaEntity();
        e.setId(log.getId()); e.setTenantId(log.getTenantId()); e.setUserId(log.getUserId());
        e.setAction(log.getAction()); e.setResourceType(log.getResourceType());
        e.setResourceId(log.getResourceId()); e.setIpAddress(log.getIpAddress());
        e.setUserAgent(log.getUserAgent()); e.setCreatedAt(log.getCreatedAt());
        jpa.save(e); return log;
    }

    @Override
    public List<AuditLog> findByTenant(UUID tenantId, int limit) {
        return jpa.findByTenantIdOrderByCreatedAtDesc(tenantId,
            org.springframework.data.domain.PageRequest.of(0, limit))
            .stream().map(this::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByResource(UUID tenantId, String resourceType, UUID resourceId) {
        return jpa.findByTenantIdAndResourceTypeAndResourceIdOrderByCreatedAtDesc(tenantId, resourceType, resourceId)
            .stream().map(this::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByUser(UUID userId, int limit) {
        return jpa.findByUserIdOrderByCreatedAtDesc(userId,
            org.springframework.data.domain.PageRequest.of(0, limit))
            .stream().map(this::toDomain).toList();
    }

    private AuditLog toDomain(AuditLogJpaEntity e) {
        return new AuditLog(e.getId(), e.getTenantId(), e.getUserId(),
                           e.getAction(), e.getResourceType(), e.getResourceId(),
                           null, null, e.getIpAddress(), e.getUserAgent());
    }
}
