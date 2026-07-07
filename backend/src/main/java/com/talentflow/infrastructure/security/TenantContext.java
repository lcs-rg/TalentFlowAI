package com.talentflow.infrastructure.security;

import java.util.UUID;

/**
 * Thread-local holder for the current company/user/team context.
 * company_id extracted from JWT — never from frontend payload.
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> COMPANY = new ThreadLocal<>();
    private static final ThreadLocal<UUID> USER = new ThreadLocal<>();
    private static final ThreadLocal<UUID> TEAM = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();
    private static final ThreadLocal<UUID> CANDIDATE = new ThreadLocal<>();

    private TenantContext() {}

    // --- Candidate context ---
    public static void setCandidate(UUID candidateId) { CANDIDATE.set(candidateId); }
    public static UUID getCandidate() { return CANDIDATE.get(); }
    public static UUID requireCandidate() {
        UUID id = CANDIDATE.get();
        if (id == null) throw new IllegalStateException("Candidate context not set");
        return id;
    }

    public static void setCompany(UUID companyId) { COMPANY.set(companyId); }
    public static UUID getCompany() { return COMPANY.get(); }
    public static UUID requireCompany() {
        UUID id = COMPANY.get();
        if (id == null) throw new IllegalStateException("Company context not set");
        return id;
    }

    public static void setUser(UUID userId) { USER.set(userId); }
    public static UUID getUser() { return USER.get(); }
    public static UUID requireUser() {
        UUID id = USER.get();
        if (id == null) throw new IllegalStateException("User context not set");
        return id;
    }

    public static void setTeam(UUID teamId) { TEAM.set(teamId); }
    public static UUID getTeam() { return TEAM.get(); }

    public static void setRole(String role) { ROLE.set(role); }
    public static String getRole() { return ROLE.get(); }

    public static void clear() {
        COMPANY.remove();
        USER.remove();
        TEAM.remove();
        ROLE.remove();
        CANDIDATE.remove();
    }
}
