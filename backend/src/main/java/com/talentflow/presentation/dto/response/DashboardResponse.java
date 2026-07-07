package com.talentflow.presentation.dto.response;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
    long activeJobs,
    long totalCandidates,
    long interviewsScheduled,
    long hiresThisMonth,
    // V2: AI-powered metrics
    double avgMatchScore,
    List<Map<String, Object>> topCandidates,
    Map<String, Long> pipelineHealth
) {
    // Backward-compatible constructor for V1 callers
    public DashboardResponse(long activeJobs, long totalCandidates, long interviewsScheduled, long hiresThisMonth) {
        this(activeJobs, totalCandidates, interviewsScheduled, hiresThisMonth, 0.0, List.of(), Map.of());
    }
}
