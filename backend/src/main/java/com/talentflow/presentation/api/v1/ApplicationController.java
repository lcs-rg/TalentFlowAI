package com.talentflow.presentation.api.v1;

import com.talentflow.domain.recruitment.*;
import com.talentflow.infrastructure.security.TenantContext;
import com.talentflow.presentation.dto.ApiResponse;
import com.talentflow.presentation.dto.response.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ApplicationController {

    private final ApplicationRepository appRepo;
    private final PipelineStageRepository stageRepo;
    private final CandidateRepository candidateRepo;

    public ApplicationController(ApplicationRepository ar, PipelineStageRepository psr, CandidateRepository cr) {
        this.appRepo = ar; this.stageRepo = psr; this.candidateRepo = cr;
    }

    @GetMapping("/api/v1/jobs/{jobId}/applications")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listByJob(
            @PathVariable UUID jobId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID stageId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Application> page = appRepo.findByJob(jobId, pageable);
        List<Map<String, Object>> data = page.map(this::toMap).toList();
        return ResponseEntity.ok(ApiResponse.ok(data, ApiResponse.Meta.of(page)));
    }

    public record CreateApplicationRequest(String name, String email, String phone) {}

    @PostMapping("/api/v1/jobs/{jobId}/applications")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(
            @PathVariable UUID jobId, @RequestBody CreateApplicationRequest req) {

        UUID companyId = TenantContext.requireCompany();
        UUID teamId = TenantContext.getTeam() != null ? TenantContext.getTeam() : UUID.randomUUID();

        // Create candidate (platform-level)
        Candidate candidate = new Candidate(UUID.randomUUID(), req.name(), req.email());
        candidate.setPhone(req.phone());
        candidateRepo.save(candidate);

        // Create application
        Application app = new Application(UUID.randomUUID(), companyId, teamId, jobId, candidate.getId());
        List<PipelineStage> stages = stageRepo.findByJobOrdered(jobId);
        if (!stages.isEmpty()) app.moveToStage(stages.get(0).getId());

        app = appRepo.save(app);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(toMap(app)));
    }

    @GetMapping("/api/v1/applications/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> get(@PathVariable UUID id) {
        Application app = appRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application não encontrada"));
        return ResponseEntity.ok(ApiResponse.ok(toMap(app)));
    }

    public record MoveRequest(UUID stageId) {}

    @PatchMapping("/api/v1/applications/{id}/move")
    public ResponseEntity<ApiResponse<Map<String, Object>>> move(
            @PathVariable UUID id, @RequestBody MoveRequest req) {
        Application app = appRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application não encontrada"));
        app.moveToStage(req.stageId);
        app = appRepo.save(app);
        return ResponseEntity.ok(ApiResponse.ok(toMap(app)));
    }

    public record FeedbackRequest(String note) {}

    @PostMapping("/api/v1/applications/{id}/feedback")
    public ResponseEntity<ApiResponse<Map<String, Object>>> feedback(
            @PathVariable UUID id, @RequestBody FeedbackRequest req) {
        Application app = appRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application não encontrada"));
        app.addFeedback(req.note);
        app = appRepo.save(app);
        return ResponseEntity.ok(ApiResponse.ok(toMap(app)));
    }

    private Map<String, Object> toMap(Application app) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", app.getId());
        m.put("jobId", app.getJobId());
        m.put("candidateId", app.getCandidateId());
        m.put("stageId", app.getStageId());
        m.put("status", app.getStatus().name());
        m.put("score", app.getScore());
        m.put("feedback", app.getFeedback());
        if (app.getStageId() != null) {
            stageRepo.findById(app.getStageId()).ifPresent(s -> m.put("stageName", s.getName()));
        }
        candidateRepo.findById(app.getCandidateId()).ifPresent(c -> {
            m.put("candidateName", c.getName());
            m.put("candidateEmail", c.getEmail());
        });
        return m;
    }
}
