package com.talentflow.domain.company;

import com.talentflow.domain.shared.BaseEntity;
import java.util.UUID;

public class CompanyUser extends BaseEntity {
    private final UUID companyId;
    private final UUID userId;
    private UUID teamId;
    private String role;

    public CompanyUser(UUID id, UUID companyId, UUID userId, UUID teamId, String role) {
        super(id);
        this.companyId = companyId;
        this.userId = userId;
        this.teamId = teamId;
        this.role = role;
    }

    public void changeTeam(UUID newTeamId) { this.teamId = newTeamId; markUpdated(); }
    public void changeRole(String newRole) { this.role = newRole; markUpdated(); }

    public UUID getCompanyId() { return companyId; }
    public UUID getUserId() { return userId; }
    public UUID getTeamId() { return teamId; }
    public String getRole() { return role; }
}
