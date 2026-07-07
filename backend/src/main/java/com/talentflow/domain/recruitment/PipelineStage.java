package com.talentflow.domain.recruitment;

import com.talentflow.domain.shared.BaseEntity;
import com.talentflow.domain.shared.TenantAware;

import java.util.UUID;

/**
 * PipelineStage — a stage in the hiring pipeline (e.g., Screening, Interview, Offer).
 * Ordered by orderIndex for Kanban display.
 */
public class PipelineStage extends BaseEntity implements TenantAware {

    private final UUID tenantId;
    private final UUID jobId;
    private String name;
    private int orderIndex;
    private StageType type;
    private String color;

    public PipelineStage(UUID id, UUID tenantId, UUID jobId, String name, int orderIndex, StageType type) {
        super(id);
        this.tenantId = tenantId;
        this.jobId = jobId;
        setName(name);
        this.orderIndex = orderIndex;
        this.type = type != null ? type : StageType.CUSTOM;
    }

    // --- Business rules ---

    public void reorder(int newIndex) {
        if (newIndex < 0) {
            throw new IllegalArgumentException("Order index must be non-negative");
        }
        this.orderIndex = newIndex;
        markUpdated();
    }

    public void rename(String newName) {
        setName(newName);
        markUpdated();
    }

    // --- Getters ---

    @Override public UUID getTenantId() { return tenantId; }
    public UUID getJobId() { return jobId; }
    public String getName() { return name; }
    public int getOrderIndex() { return orderIndex; }
    public StageType getType() { return type; }
    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    private void setName(String name) {
        if (name == null || name.trim().length() < 2 || name.trim().length() > 255) {
            throw new IllegalArgumentException("Stage name must be between 2 and 255 characters");
        }
        this.name = name.trim();
    }
}
