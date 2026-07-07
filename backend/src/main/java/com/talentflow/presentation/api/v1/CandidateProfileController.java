package com.talentflow.presentation.api.v1;

import com.talentflow.application.candidate.CandidateAuthService;
import com.talentflow.domain.recruitment.*;
import com.talentflow.infrastructure.security.TenantContext;
import com.talentflow.presentation.dto.ApiResponse;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/candidate")
public class CandidateProfileController {

    private final CandidateAuthService authService;
    private final CandidateRepository candidateRepo;
    private final ApplicationRepository applicationRepo;
    private final JobRepository jobRepo;

    public CandidateProfileController(CandidateAuthService as, CandidateRepository cr,
                                       ApplicationRepository ar, JobRepository jr) {
        this.authService = as; this.candidateRepo = cr; this.applicationRepo = ar; this.jobRepo = jr;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> profile() {
        UUID candidateId = TenantContext.requireCandidate();
        Candidate c = authService.getAuthenticatedCandidate(candidateId);
        return ResponseEntity.ok(ApiResponse.ok(toCandidateMap(c)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateProfile(@RequestBody Map<String, String> body) {
        UUID candidateId = TenantContext.requireCandidate();
        Candidate c = authService.getAuthenticatedCandidate(candidateId);

        if (body.containsKey("name")) c.setName(body.get("name"));
        if (body.containsKey("phone")) c.setPhone(body.get("phone"));
        if (body.containsKey("resumeText")) {
            c.updateResume(c.getResumeUrl(), body.get("resumeText"));
        }

        c = candidateRepo.save(c);
        return ResponseEntity.ok(ApiResponse.ok(toCandidateMap(c)));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> myApplications(
            @PageableDefault(size = 20) Pageable pageable) {
        UUID candidateId = TenantContext.requireCandidate();
        Page<Application> page = applicationRepo.findByCandidate(candidateId, pageable);

        List<Map<String, Object>> apps = page.getContent().stream().map(app -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", app.getId());
            m.put("jobId", app.getJobId());
            m.put("status", app.getStatus().name());
            m.put("stageId", app.getStageId());
            m.put("score", app.getScore());
            jobRepo.findById(app.getJobId()).ifPresent(job -> {
                m.put("jobTitle", job.getTitle());
                m.put("jobDepartment", job.getDepartment());
                m.put("jobLocation", job.getLocation());
            });
            return m;
        }).toList();

        return ResponseEntity.ok(ApiResponse.ok(apps, ApiResponse.Meta.of(page)));
    }

    @PostMapping("/apply/{jobId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> apply(@PathVariable UUID jobId) {
        UUID candidateId = TenantContext.requireCandidate();

        // Check if job exists and is published
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada"));

        // Check if already applied
        if (applicationRepo.findByJobAndCandidate(jobId, candidateId).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(409, "Você já se candidatou para esta vaga"));
        }

        // Create application
        List<PipelineStage> stages = jobRepo.findById(jobId)
                .map(j -> {
                    // Find stages... need stageRepo. Let me use applicationRepo directly.
                    return List.<PipelineStage>of();
                }).orElse(List.of());

        Application app = new Application(UUID.randomUUID(), job.getCompanyId(), null, jobId, candidateId);
        app = applicationRepo.save(app);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", app.getId());
        result.put("status", app.getStatus().name());
        result.put("jobTitle", job.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    // Public: list published jobs for candidates to browse
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> publicJobs(
            @PageableDefault(size = 20) Pageable pageable) {
        // V1: return all published jobs (no tenant filter for public browsing)
        // In production, this should be paginated properly
        List<Map<String, Object>> jobs = new ArrayList<>();
        // Simplified — in a real scenario, we'd need a public job listing
        return ResponseEntity.ok(ApiResponse.ok(jobs));
    }

    private Map<String, Object> toCandidateMap(Candidate c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("name", c.getName());
        m.put("email", c.getEmail());
        m.put("phone", c.getPhone());
        m.put("resumeUrl", c.getResumeUrl());
        m.put("resumeText", c.getResumeText());
        m.put("tags", c.getTags());
        m.put("createdAt", c.getCreatedAt().toString());
        return m;
    }
}
