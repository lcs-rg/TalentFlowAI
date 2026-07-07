package com.talentflow.domain.tenant;

import com.talentflow.domain.shared.BaseEntity;

import java.util.UUID;

/**
 * Tenant aggregate root — represents an isolated company account.
 * All data in the system is scoped to a tenant.
 */
public class Tenant extends BaseEntity {

    private String slug;
    private String name;
    private TenantPlan plan;
    private TenantStatus status;

    public Tenant(UUID id, String slug, String name, TenantPlan plan) {
        super(id);
        setSlug(slug);
        setName(name);
        this.plan = plan != null ? plan : TenantPlan.FREE;
        this.status = TenantStatus.ACTIVE;
    }

    public Tenant(String slug, String name, TenantPlan plan) {
        this(UUID.randomUUID(), slug, name, plan);
    }

    // --- Business rules ---

    public void suspend() {
        if (this.status == TenantStatus.DELETED) {
            throw new IllegalStateException("Cannot suspend a deleted tenant");
        }
        this.status = TenantStatus.SUSPENDED;
        markUpdated();
    }

    public void reactivate() {
        if (this.status != TenantStatus.SUSPENDED) {
            throw new IllegalStateException("Can only reactivate a suspended tenant");
        }
        this.status = TenantStatus.ACTIVE;
        markUpdated();
    }

    public void upgradePlan(TenantPlan newPlan) {
        if (newPlan == this.plan) {
            throw new IllegalArgumentException("Tenant is already on plan: " + newPlan);
        }
        this.plan = newPlan;
        markUpdated();
    }

    public void markDeleted() {
        this.status = TenantStatus.DELETED;
        markUpdated();
    }

    // --- Getters & setters ---

    public String getSlug() { return slug; }
    public String getName() { return name; }
    public TenantPlan getPlan() { return plan; }
    public TenantStatus getStatus() { return status; }

    private void setSlug(String slug) {
        if (slug == null || !slug.matches("^[a-z0-9]+(-[a-z0-9]+)*$")) {
            throw new IllegalArgumentException("Slug must be lowercase alphanumeric with hyphens");
        }
        this.slug = slug;
    }

    private void setName(String name) {
        if (name == null || name.trim().length() < 2 || name.trim().length() > 255) {
            throw new IllegalArgumentException("Tenant name must be between 2 and 255 characters");
        }
        this.name = name.trim();
    }
}
