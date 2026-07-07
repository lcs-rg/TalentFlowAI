package com.talentflow.application.ai;

import com.talentflow.infrastructure.ai.AIServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);
    private final AIServiceClient aiClient;
    private final JdbcTemplate jdbc;

    public EmbeddingService(AIServiceClient ai, JdbcTemplate jdbc) {
        this.aiClient = ai; this.jdbc = jdbc;
    }

    /**
     * Generate and store embedding for a job.
     */
    public void embedJob(UUID jobId, String title, String description) {
        String text = (title != null ? title : "") + " " + (description != null ? description : "");
        var result = aiClient.embed(text);
        if (result.success() && result.embedding() != null) {
            String vector = arrayToPgVector(result.embedding());
            jdbc.update("""
                INSERT INTO job_embeddings (id, job_id, embedding, model_version, created_at)
                VALUES (?, ?, ?::vector, 'all-MiniLM-L6-v2', NOW())
                ON CONFLICT (job_id) DO UPDATE SET embedding = ?::vector, created_at = NOW()
                """, UUID.randomUUID(), jobId, vector, vector);
            log.info("Job embedding stored: jobId={}, dims={}", jobId, result.dimensions());
        } else {
            log.warn("Failed to generate embedding for jobId={}", jobId);
        }
    }

    /**
     * Generate and store embedding for a candidate.
     */
    public void embedCandidate(UUID candidateId, String resumeText) {
        if (resumeText == null || resumeText.isBlank()) return;
        var result = aiClient.embed(resumeText);
        if (result.success() && result.embedding() != null) {
            String vector = arrayToPgVector(result.embedding());
            jdbc.update("""
                INSERT INTO candidate_embeddings (id, candidate_id, embedding, model_version, created_at)
                VALUES (?, ?, ?::vector, 'all-MiniLM-L6-v2', NOW())
                ON CONFLICT (candidate_id) DO UPDATE SET embedding = ?::vector, created_at = NOW()
                """, UUID.randomUUID(), candidateId, vector, vector);
            log.info("Candidate embedding stored: candidateId={}, dims={}", candidateId, result.dimensions());
        } else {
            log.warn("Failed to generate embedding for candidateId={}", candidateId);
        }
    }

    /**
     * Semantic search: find top-K candidates similar to a job.
     * Returns candidate IDs ordered by cosine similarity (highest first).
     */
    public List<UUID> searchCandidatesByJob(UUID jobId, int topK) {
        // Get job embedding
        var rows = jdbc.queryForList(
            "SELECT embedding FROM job_embeddings WHERE job_id = ?", jobId);
        if (rows.isEmpty()) return List.of();

        // Query candidate embeddings by cosine similarity
        List<Map<String, Object>> results = jdbc.queryForList("""
            SELECT ce.candidate_id, 1 - (ce.embedding <=> (SELECT embedding FROM job_embeddings WHERE job_id = ?)) AS similarity
            FROM candidate_embeddings ce
            WHERE ce.embedding IS NOT NULL
            ORDER BY similarity DESC
            LIMIT ?
            """, jobId, topK);

        return results.stream()
                .map(r -> (UUID) r.get("candidate_id"))
                .toList();
    }

    /**
     * Semantic search: find top-K jobs similar to a candidate's resume.
     */
    public List<UUID> searchJobsByCandidate(UUID candidateId, int topK) {
        var rows = jdbc.queryForList(
            "SELECT embedding FROM candidate_embeddings WHERE candidate_id = ?", candidateId);
        if (rows.isEmpty()) return List.of();

        List<Map<String, Object>> results = jdbc.queryForList("""
            SELECT je.job_id, 1 - (je.embedding <=> (SELECT embedding FROM candidate_embeddings WHERE candidate_id = ?)) AS similarity
            FROM job_embeddings je
            WHERE je.embedding IS NOT NULL
            ORDER BY similarity DESC
            LIMIT ?
            """, candidateId, topK);

        return results.stream()
                .map(r -> (UUID) r.get("job_id"))
                .toList();
    }

    private String arrayToPgVector(double[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(arr[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
