package com.talentflow.domain.recruitment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PipelineStageRepository {
    PipelineStage save(PipelineStage stage);
    Optional<PipelineStage> findById(UUID id);
    List<PipelineStage> findByJob(UUID jobId);
    List<PipelineStage> findByJobOrdered(UUID jobId);
    void delete(PipelineStage stage);
    void deleteByJob(UUID jobId);
}
