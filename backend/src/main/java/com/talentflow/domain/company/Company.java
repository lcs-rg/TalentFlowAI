package com.talentflow.domain.company;

import com.talentflow.domain.shared.BaseEntity;
import com.talentflow.domain.shared.TenantAware;

import java.util.UUID;

/**
 * Company aggregate — profile for a tenant.
 */
public class Company extends BaseEntity implements TenantAware {

    private final UUID tenantId;
    private String name;
    private String logoUrl;
    private String industry;
    private CompanySize size;
    private String website;

    public Company(UUID id, UUID tenantId, String name) {
        super(id);
        this.tenantId = tenantId;
        setName(name);
    }

    // --- Business rules ---

    public void updateProfile(String name, String industry, CompanySize size, String website) {
        setName(name);
        this.industry = industry;
        this.size = size;
        this.website = website;
        markUpdated();
    }

    public void updateLogo(String logoUrl) {
        this.logoUrl = logoUrl;
        markUpdated();
    }

    // --- Getters ---

    @Override
    public UUID getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getLogoUrl() { return logoUrl; }
    public String getIndustry() { return industry; }
    public CompanySize getSize() { return size; }
    public String getWebsite() { return website; }

    private void setName(String name) {
        if (name == null || name.trim().length() < 2) {
            throw new IllegalArgumentException("Company name must be at least 2 characters");
        }
        this.name = name.trim();
    }
}
