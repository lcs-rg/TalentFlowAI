package com.talentflow.presentation.api.v1;

import com.talentflow.domain.recruitment.*;
import com.talentflow.infrastructure.ai.AIServiceClient;
import com.talentflow.infrastructure.security.TenantContext;
import com.talentflow.presentation.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/ai")
public class AIController {

    private final AIServiceClient aiClient;
    private final JobRepository jobRepo;
    private final CandidateRepository candidateRepo;
    private final ApplicationRepository applicationRepo;

    public AIController(AIServiceClient ai, JobRepository jr, CandidateRepository cr, ApplicationRepository ar) {
        this.aiClient = ai; this.jobRepo = jr; this.candidateRepo = cr; this.applicationRepo = ar;
    }

    @PostMapping("/match/{jobId}/{candidateId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> match(
            @PathVariable UUID jobId, @PathVariable UUID candidateId) {
        TenantContext.requireCompany();

        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada"));
        Candidate candidate = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidato não encontrado"));

        String jobDesc = job.getTitle() + "\n" + (job.getDescription() != null ? job.getDescription() : "");
        String resumeText = candidate.getResumeText() != null ? candidate.getResumeText() : "";

        var result = aiClient.match(jobDesc, resumeText);
        if (!result.success()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Falha na análise de compatibilidade"));
        }

        // Update application score if one exists for this job+candidate pair
        applicationRepo.findByJobAndCandidate(jobId, candidateId).ifPresent(app -> {
            if (result.data() != null && result.data().get("overall_score") instanceof Number score) {
                app.setScore(score.floatValue());
                applicationRepo.save(app);
            }
        });

        Map<String, Object> response = new LinkedHashMap<>(result.data() != null ? result.data() : Map.of());
        response.put("metadata", result.metadata());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/parse-resume")
    public ResponseEntity<ApiResponse<Map<String, Object>>> parseResume(@RequestBody Map<String, String> body) {
        String resumeText = body.get("resumeText");
        if (resumeText == null || resumeText.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Texto do currículo é obrigatório"));
        }

        var result = aiClient.parseResume(resumeText);
        if (!result.success()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Falha no parsing do currículo"));
        }

        Map<String, Object> response = new LinkedHashMap<>(result.data() != null ? result.data() : Map.of());
        response.put("metadata", result.metadata());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/screening/{jobId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> screening(@PathVariable UUID jobId) {
        TenantContext.requireCompany();

        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada"));

        String jobDesc = job.getTitle() + "\n" + (job.getDescription() != null ? job.getDescription() : "");
        List<Application> apps = applicationRepo.findByJob(jobId, org.springframework.data.domain.Pageable.unpaged()).getContent();

        List<Map<String, Object>> results = new ArrayList<>();
        for (Application app : apps) {
            candidateRepo.findById(app.getCandidateId()).ifPresent(candidate -> {
                String resumeText = candidate.getResumeText() != null ? candidate.getResumeText() : "";
                var result = aiClient.match(jobDesc, resumeText);
                if (result.success() && result.data() != null) {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("applicationId", app.getId());
                    entry.put("candidateId", candidate.getId());
                    entry.put("candidateName", candidate.getName());
                    entry.put("score", result.data().getOrDefault("overall_score", 0));
                    results.add(entry);

                    if (result.data().get("overall_score") instanceof Number score) {
                        app.setScore(score.floatValue());
                        applicationRepo.save(app);
                    }
                }
            });
        }

        results.sort((a, b) -> {
            double sa = ((Number) a.getOrDefault("score", 0)).doubleValue();
            double sb = ((Number) b.getOrDefault("score", 0)).doubleValue();
            return Double.compare(sb, sa);
        });

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("jobId", jobId);
        response.put("total", results.size());
        response.put("results", results);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
