package com.talentflow.presentation.api.v1;

import com.talentflow.domain.recruitment.*;
import com.talentflow.infrastructure.security.TenantContext;
import com.talentflow.presentation.dto.ApiResponse;
import com.talentflow.presentation.dto.response.InterviewResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
public class InterviewController {

    private final InterviewRepository interviewRepo;
    private final ApplicationRepository applicationRepo;

    public InterviewController(InterviewRepository ir, ApplicationRepository ar) {
        this.interviewRepo = ir; this.applicationRepo = ar;
    }

    @GetMapping("/api/v1/applications/{applicationId}/interviews")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> list(@PathVariable UUID applicationId) {
        Application app = findApp(applicationId);
        List<Interview> interviews = interviewRepo.findByCandidate(app.getCandidateId());
        List<Map<String, Object>> result = interviews.stream().map(this::toMap).toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    public record ScheduleRequest(String scheduledAt, String type) {}

    @PostMapping("/api/v1/applications/{applicationId}/interviews")
    public ResponseEntity<ApiResponse<Map<String, Object>>> schedule(
            @PathVariable UUID applicationId, @RequestBody ScheduleRequest req) {
        Application app = findApp(applicationId);

        Interview interview = new Interview(UUID.randomUUID(), app.getCandidateId(),
                req.type != null ? InterviewType.valueOf(req.type.toUpperCase()) : InterviewType.VIDEO);

        if (req.scheduledAt != null) {
            interview.schedule(Instant.parse(req.scheduledAt));
        }

        interview = interviewRepo.save(interview);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(toMap(interview)));
    }

    public record UpdateInterviewRequest(String scheduledAt, String type, String status) {}

    @PatchMapping("/api/v1/interviews/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(
            @PathVariable UUID id, @RequestBody UpdateInterviewRequest req) {
        Interview interview = findInterview(id);

        if (req.scheduledAt != null) {
            interview.schedule(Instant.parse(req.scheduledAt));
        }
        if (req.status != null) {
            InterviewStatus newStatus = InterviewStatus.valueOf(req.status.toUpperCase());
            switch (newStatus) {
                case CONFIRMED -> interview.confirm();
                case CANCELLED -> interview.cancel();
                case COMPLETED -> interview.complete(Map.of());
            }
        }

        interview = interviewRepo.save(interview);
        return ResponseEntity.ok(ApiResponse.ok(toMap(interview)));
    }

    @DeleteMapping("/api/v1/interviews/{id}")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable UUID id) {
        Interview interview = findInterview(id);
        interview.cancel();
        interviewRepo.save(interview);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/api/v1/interviews/{id}/ai-questions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> aiQuestions(@PathVariable UUID id) {
        Interview interview = findInterview(id);
        // V1: generate placeholder questions (real AI integration in V3)
        List<Map<String, String>> questions = List.of(
                Map.of("question", "Conte-me sobre sua experiência mais relevante para esta vaga.", "category", "experiencia", "difficulty", "medium"),
                Map.of("question", "Como você lida com prazos apertados e múltiplas prioridades?", "category", "comportamental", "difficulty", "medium"),
                Map.of("question", "Descreva um desafio técnico que você resolveu recentemente.", "category", "tecnica", "difficulty", "hard")
        );
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("interviewId", id);
        result.put("questions", questions);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // --- helpers ---

    private Application findApp(UUID id) {
        return applicationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application não encontrada"));
    }

    private Interview findInterview(UUID id) {
        return interviewRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrevista não encontrada"));
    }

    private Map<String, Object> toMap(Interview i) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", i.getId());
        m.put("candidateId", i.getCandidateId());
        m.put("type", i.getType().name());
        m.put("status", i.getStatus().name());
        m.put("scheduledAt", i.getScheduledAt() != null ? i.getScheduledAt().toString() : null);
        m.put("createdAt", i.getCreatedAt().toString());
        return m;
    }
}
