package com.talentflow.domain.ai;

import java.util.List;

/**
 * Domain representation of a matching result between a job and a candidate.
 * Pure domain — no AI provider dependency.
 */
public class MatchingResult {

    private final double overallScore;
    private final double skillsMatch;
    private final double experienceMatch;
    private final double educationMatch;
    private final String explanation;
    private final List<String> strengths;
    private final List<String> gaps;

    public MatchingResult(double overallScore, double skillsMatch, double experienceMatch,
                          double educationMatch, String explanation,
                          List<String> strengths, List<String> gaps) {
        this.overallScore = clamp(overallScore);
        this.skillsMatch = clamp(skillsMatch);
        this.experienceMatch = clamp(experienceMatch);
        this.educationMatch = clamp(educationMatch);
        this.explanation = explanation;
        this.strengths = strengths != null ? List.copyOf(strengths) : List.of();
        this.gaps = gaps != null ? List.copyOf(gaps) : List.of();
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    public double getOverallScore() { return overallScore; }
    public double getSkillsMatch() { return skillsMatch; }
    public double getExperienceMatch() { return experienceMatch; }
    public double getEducationMatch() { return educationMatch; }
    public String getExplanation() { return explanation; }
    public List<String> getStrengths() { return strengths; }
    public List<String> getGaps() { return gaps; }

    public boolean isStrongMatch() { return overallScore >= 0.7; }
    public boolean isGoodMatch() { return overallScore >= 0.5; }
    public boolean isWeakMatch() { return overallScore < 0.3; }
}
