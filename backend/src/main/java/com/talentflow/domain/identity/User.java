package com.talentflow.domain.identity;

import com.talentflow.domain.shared.BaseEntity;
import com.talentflow.domain.shared.TenantAware;

import java.util.UUID;

/**
 * User entity — belongs to a tenant, has a role for RBAC.
 */
public class User extends BaseEntity implements TenantAware {

    private final UUID tenantId;
    private String email;
    private String passwordHash;
    private String name;
    private Role role;
    private String avatarUrl;
    private boolean enabled;

    public User(UUID id, UUID tenantId, String email, String passwordHash, String name, Role role) {
        super(id);
        this.tenantId = tenantId;
        setEmail(email);
        this.passwordHash = passwordHash;
        setName(name);
        this.role = role != null ? role : Role.RECRUITER;
        this.enabled = true;
    }

    // --- Business rules ---

    public void changeRole(Role newRole) {
        this.role = newRole;
        markUpdated();
    }

    public void disable() {
        this.enabled = false;
        markUpdated();
    }

    public void enable() {
        this.enabled = true;
        markUpdated();
    }

    public boolean canManageJobs() {
        return role == Role.ADMIN || role == Role.RECRUITER;
    }

    public boolean canManageUsers() {
        return role == Role.ADMIN;
    }

    // --- Getters ---

    @Override
    public UUID getTenantId() { return tenantId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
    public Role getRole() { return role; }
    public String getAvatarUrl() { return avatarUrl; }
    public boolean isEnabled() { return enabled; }

    private void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        this.email = email.toLowerCase().trim();
    }

    private void setName(String name) {
        if (name == null || name.trim().length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters");
        }
        this.name = name.trim();
    }
}
