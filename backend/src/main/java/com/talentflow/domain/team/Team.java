package com.talentflow.domain.team;

import com.talentflow.domain.shared.BaseEntity;
import java.util.UUID;

public class Team extends BaseEntity {
    private final UUID companyId;
    private String name;

    public Team(UUID id, UUID companyId, String name) {
        super(id);
        this.companyId = companyId;
        setName(name);
    }

    public void rename(String newName) { setName(newName); markUpdated(); }

    public UUID getCompanyId() { return companyId; }
    public String getName() { return name; }

    private void setName(String name) {
        if (name == null || name.trim().length() < 2) throw new IllegalArgumentException("Team name must be at least 2 characters");
        this.name = name.trim();
    }
}
