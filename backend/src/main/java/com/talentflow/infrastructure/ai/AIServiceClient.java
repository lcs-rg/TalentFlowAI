package com.talentflow.infrastructure.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Component
public class AIServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AIServiceClient.class);
    private final RestClient restClient;

    public AIServiceClient(@Value("${app.ai-service.url}") String aiServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(aiServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public record ParseResumeResponse(boolean success, Map<String, Object> data, Map<String, Object> error, Map<String, Object> metadata) {}

    public ParseResumeResponse parseResume(String resumeText) {
        log.info("Calling AI parse-resume");
        try {
            return restClient.post()
                    .uri("/parse-resume")
                    .body(Map.of("resume_text", resumeText))
                    .retrieve()
                    .body(ParseResumeResponse.class);
        } catch (Exception e) {
            log.error("AI parse-resume failed: {}", e.getMessage());
            return new ParseResumeResponse(false, null, Map.of("code", "AI_SERVICE_ERROR", "message", e.getMessage()), null);
        }
    }

    public record EmbedResponse(boolean success, double[] embedding, int dimensions, String model, Map<String, Object> error) {}

    public EmbedResponse embed(String text) {
        log.info("Calling AI embed, text length={}", text != null ? text.length() : 0);
        try {
            return restClient.post()
                    .uri("/embed")
                    .body(Map.of("text", text))
                    .retrieve()
                    .body(EmbedResponse.class);
        } catch (Exception e) {
            log.error("AI embed failed: {}", e.getMessage());
            return new EmbedResponse(false, null, 0, null,
                    Map.of("code", "EMBED_ERROR", "message", e.getMessage()));
        }
    }

    public record MatchResponse(boolean success, Map<String, Object> data, Map<String, Object> error, Map<String, Object> metadata) {}

    public MatchResponse match(String jobDescription, String candidateResume) {
        log.info("Calling AI match");
        try {
            return restClient.post()
                    .uri("/match")
                    .body(Map.of("job_description", jobDescription, "candidate_resume", candidateResume))
                    .retrieve()
                    .body(MatchResponse.class);
        } catch (Exception e) {
            log.error("AI match failed: {}", e.getMessage());
            return new MatchResponse(false, null, Map.of("code", "AI_SERVICE_ERROR", "message", e.getMessage()), null);
        }
    }
}
