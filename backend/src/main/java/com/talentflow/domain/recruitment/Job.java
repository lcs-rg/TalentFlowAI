package com.talentflow.domain.recruitment;

import com.talentflow.domain.shared.BaseEntity;
import com.talentflow.domain.shared.TenantAware;

import java.time.Instant;
import java.util.*;

/**
 * Job aggregate root — a job posting in the hiring pipeline.
 */
public class Job extends BaseEntity implements TenantAware {

    private final UUID companyId;
    private UUID teamId;
    private String title;
    private String description;
    private String department;
    private String location;
    private JobType type;
    private Integer salaryMin;
    private Integer salaryMax;
    private String currency;
    private List<String> requirements;
    private List<String> benefits;
    private JobStatus status;
    private UUID createdBy;
    private Instant publishedAt;
    private Instant closedAt;

    public Job(UUID id, UUID companyId, String title, String description, JobType type) {
        super(id);
        this.companyId = companyId;
        setTitle(title);
        setDescription(description);
        this.type = type != null ? type : JobType.FULL_TIME;
        this.status = JobStatus.DRAFT;
        this.requirements = new ArrayList<>();
        this.benefits = new ArrayList<>();
    }

    // --- Business rules ---

    public void publish() {
        if (this.status != JobStatus.DRAFT) {
            throw new IllegalStateException("Only draft jobs can be published. Current status: " + status);
        }
        if (this.title == null || this.description == null) {
            throw new IllegalStateException("Title and description are required to publish");
        }
        this.status = JobStatus.PUBLISHED;
        this.publishedAt = Instant.now();
        markUpdated();
    }

    public void close() {
        if (this.status != JobStatus.PUBLISHED) {
            throw new IllegalStateException("Only published jobs can be closed");
        }
        this.status = JobStatus.CLOSED;
        this.closedAt = Instant.now();
        markUpdated();
    }

    public void archive() {
        if (this.status != JobStatus.CLOSED) {
            throw new IllegalStateException("Only closed jobs can be archived");
        }
        this.status = JobStatus.ARCHIVED;
        markUpdated();
    }

    public void reopen() {
        if (this.status != JobStatus.CLOSED) {
            throw new IllegalStateException("Only closed jobs can be reopened");
        }
        this.status = JobStatus.PUBLISHED;
        this.closedAt = null;
        markUpdated();
    }

    public void updateDetails(String title, String description, String department, String location,
                               JobType type, Integer salaryMin, Integer salaryMax, String currency,
                               List<String> requirements, List<String> benefits) {
        setTitle(title);
        setDescription(description);
        this.department = department;
        this.location = location;
        this.type = type;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.currency = currency;
        this.requirements = requirements != null ? new ArrayList<>(requirements) : new ArrayList<>();
        this.benefits = benefits != null ? new ArrayList<>(benefits) : new ArrayList<>();
        markUpdated();
    }

    // --- Getters ---

    @Override public UUID getTenantId() { return companyId; }
    public UUID getCompanyId() { return companyId; }
    public UUID getTeamId() { return teamId; }
    public void setTeamId(UUID teamId) { this.teamId = teamId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDepartment() { return department; }
    public String getLocation() { return location; }
    public JobType getType() { return type; }
    public Integer getSalaryMin() { return salaryMin; }
    public Integer getSalaryMax() { return salaryMax; }
    public String getCurrency() { return currency; }
    public List<String> getRequirements() { return Collections.unmodifiableList(requirements); }
    public List<String> getBenefits() { return Collections.unmodifiableList(benefits); }
    public JobStatus getStatus() { return status; }
    public UUID getCreatedBy() { return createdBy; }
    public Instant getPublishedAt() { return publishedAt; }
    public Instant getClosedAt() { return closedAt; }

    public void setDepartment(String department) { this.department = department; }
    public void setLocation(String location) { this.location = location; }
    public void setSalaryMin(Integer v) { this.salaryMin = v; }
    public void setSalaryMax(Integer v) { this.salaryMax = v; }
    public void setCurrency(String v) { this.currency = v; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }

    private void setTitle(String title) {
        if (title == null || title.trim().length() < 5 || title.trim().length() > 255) {
            throw new IllegalArgumentException("Title must be between 5 and 255 characters");
        }
        this.title = title.trim();
    }

    private void setDescription(String description) {
        if (description == null || description.trim().length() < 20) {
            throw new IllegalArgumentException("Description must be at least 20 characters");
        }
        this.description = description.trim();
    }
}
