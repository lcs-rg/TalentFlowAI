package com.talentflow.presentation.dto.response;
import java.util.List;
public record ParsedResumeResponse(String fullName, String email, String phone, String summary,
    List<String> skills, List<ExperienceEntry> experience, List<EducationEntry> education) {
    public record ExperienceEntry(String company, String title, String startDate, String endDate, String description) {}
    public record EducationEntry(String institution, String degree, String field, String year) {}
}