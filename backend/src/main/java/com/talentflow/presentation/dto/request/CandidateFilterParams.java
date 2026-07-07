package com.talentflow.presentation.dto.request;
import java.util.UUID;
public record CandidateFilterParams(String status, UUID stageId, String search) {}