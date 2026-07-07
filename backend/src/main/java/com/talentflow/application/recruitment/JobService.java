package com.talentflow.application.recruitment;

import com.talentflow.domain.recruitment.*;
import com.talentflow.infrastructure.security.TenantContext;
import com.talentflow.presentation.dto.request.*;
import com.talentflow.presentation.dto.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);
    private final JobRepository jobRepo;
    private final PipelineStageRepository stageRepo;

    public JobService(JobRepository jr, PipelineStageRepository psr) {
        this.jobRepo = jr; this.stageRepo = psr;
    }

    @Transactional
    public JobResponse createJob(CreateJobRequest req) {
        UUID companyId = TenantContext.requireCompany();
        Job job = new Job(UUID.randomUUID(), companyId, req.title(), req.description(),
                req.type() != null ? JobType.valueOf(req.type()) : JobType.FULL_TIME);
        job.setDepartment(req.department()); job.setLocation(req.location());
        job = jobRepo.save(job);
        createDefaultStages(companyId, job.getId());
        log.info("Job created: id={}", job.getId());
        return toResponse(job);
    }

    public Page<JobResponse> listJobs(JobFilterParams f, Pageable pageable) {
        UUID companyId = TenantContext.requireCompany();
        return jobRepo.findByTenantWithFilters(companyId, f.status(), f.search(), f.type(), pageable)
                .map(this::toResponse);
    }

    public JobResponse getJob(UUID id) {
        return toResponse(findActive(id));
    }

    @Transactional
    public JobResponse updateJob(UUID id, UpdateJobRequest req) {
        Job job = findActive(id);
        job.updateDetails(req.title(), req.description(), req.department(), req.location(),
                req.type() != null ? JobType.valueOf(req.type()) : job.getType(),
                req.salaryMin(), req.salaryMax(), req.currency(), null, null);
        return toResponse(jobRepo.save(job));
    }

    @Transactional public JobResponse publish(UUID id) { Job j = findActive(id); j.publish(); return toResponse(jobRepo.save(j)); }
    @Transactional public void close(UUID id) { Job j = findActive(id); j.close(); jobRepo.save(j); }
    @Transactional public void softDelete(UUID id) { jobRepo.softDelete(id, TenantContext.getUser(), java.time.Instant.now()); }

    // Pipeline
    public List<PipelineStageResponse> getPipeline(UUID jobId) {
        return stageRepo.findByJobOrdered(jobId).stream()
                .map(s -> new PipelineStageResponse(s.getId(), s.getName(), s.getOrderIndex(), s.getType().name(), s.getColor())).toList();
    }

    @Transactional
    public PipelineStageResponse createStage(UUID jobId, CreatePipelineStageRequest req) {
        UUID companyId = TenantContext.requireCompany();
        List<PipelineStage> existing = stageRepo.findByJobOrdered(jobId);
        PipelineStage s = new PipelineStage(UUID.randomUUID(), companyId, jobId, req.name(), existing.size(),
                req.type() != null ? StageType.valueOf(req.type()) : StageType.CUSTOM);
        s.setColor(req.color());
        s = stageRepo.save(s);
        return new PipelineStageResponse(s.getId(), s.getName(), s.getOrderIndex(), s.getType().name(), s.getColor());
    }

    @Transactional
    public void reorderPipeline(UUID jobId, ReorderPipelineRequest req) {
        for (int i = 0; i < req.stageIds().size(); i++) {
            PipelineStage s = stageRepo.findById(req.stageIds().get(i)).orElseThrow();
            s.reorder(i); stageRepo.save(s);
        }
    }

    // Dashboard
    public DashboardResponse getDashboard() {
        UUID companyId = TenantContext.requireCompany();
        long activeJobs = jobRepo.countByCompanyAndStatus(companyId, JobStatus.PUBLISHED);
        return new DashboardResponse(activeJobs, 0, 0, 0);
    }

    // Helpers
    private Job findActive(UUID id) { return jobRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada")); }

    private void createDefaultStages(UUID companyId, UUID jobId) {
        String[][] d = {{"Triagem","SCREENING","#6B7280"},{"Entrevista RH","INTERVIEW","#3B82F6"},{"Entrevista Técnica","TECHNICAL","#8B5CF6"},{"Avaliação","ASSESSMENT","#F59E0B"},{"Proposta","OFFER","#10B981"}};
        for (int i = 0; i < d.length; i++) {
            PipelineStage s = new PipelineStage(UUID.randomUUID(), companyId, jobId, d[i][0], i, StageType.valueOf(d[i][1]));
            s.setColor(d[i][2]); stageRepo.save(s);
        }
    }

    private JobResponse toResponse(Job j) {
        return new JobResponse(j.getId(), j.getTitle(), j.getDescription(), j.getDepartment(), j.getLocation(),
                j.getType().name(), j.getSalaryMin(), j.getSalaryMax(), j.getCurrency(), j.getStatus().name(),
                j.getCreatedAt(), j.getUpdatedAt(), j.getPublishedAt(), 0);
    }
}
