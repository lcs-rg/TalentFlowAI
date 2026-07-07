package com.talentflow.presentation.api.v1;

import com.talentflow.application.ai.EmbeddingService;
import com.talentflow.domain.recruitment.*;
import com.talentflow.infrastructure.security.TenantContext;
import com.talentflow.presentation.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/ai")
public class SemanticSearchController {

    private final EmbeddingService embeddingService;
    private final JobRepository jobRepo;
    private final CandidateRepository candidateRepo;

    public SemanticSearchController(EmbeddingService es, JobRepository jr, CandidateRepository cr) {
        this.embeddingService = es; this.jobRepo = jr; this.candidateRepo = cr;
    }

    @PostMapping("/embed-job/{jobId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> embedJob(@PathVariable UUID jobId) {
        TenantContext.requireCompany();
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada"));
        embeddingService.embedJob(jobId, job.getTitle(), job.getDescription());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("jobId", jobId, "status", "embedded")));
    }

    @PostMapping("/embed-candidate/{candidateId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> embedCandidate(@PathVariable UUID candidateId) {
        Candidate c = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidato não encontrado"));
        embeddingService.embedCandidate(candidateId, c.getResumeText());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("candidateId", candidateId, "status", "embedded")));
    }

    @GetMapping("/search-candidates/{jobId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchCandidates(
            @PathVariable UUID jobId,
            @RequestParam(defaultValue = "10") int topK) {
        TenantContext.requireCompany();
        List<UUID> candidateIds = embeddingService.searchCandidatesByJob(jobId, topK);

        List<Map<String, Object>> results = new ArrayList<>();
        for (UUID cid : candidateIds) {
            candidateRepo.findById(cid).ifPresent(c -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", c.getId());
                m.put("name", c.getName());
                m.put("email", c.getEmail());
                m.put("resumeText", c.getResumeText() != null
                        ? c.getResumeText().substring(0, Math.min(200, c.getResumeText().length())) : "");
                results.add(m);
            });
        }
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/search-jobs/{candidateId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchJobs(
            @PathVariable UUID candidateId,
            @RequestParam(defaultValue = "10") int topK) {
        List<UUID> jobIds = embeddingService.searchJobsByCandidate(candidateId, topK);

        List<Map<String, Object>> results = new ArrayList<>();
        for (UUID jid : jobIds) {
            jobRepo.findById(jid).ifPresent(j -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", j.getId());
                m.put("title", j.getTitle());
                m.put("department", j.getDepartment());
                m.put("location", j.getLocation());
                m.put("status", j.getStatus().name());
                results.add(m);
            });
        }
        return ResponseEntity.ok(ApiResponse.ok(results));
    }
}
