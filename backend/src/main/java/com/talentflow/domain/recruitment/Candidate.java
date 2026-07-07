package com.talentflow.domain.recruitment;

import com.talentflow.domain.shared.BaseEntity;
import java.util.*;

/**
 * Candidate entity — platform-level (not tied to a specific job).
 * Applications link Candidates to Jobs.
 */
public class Candidate extends BaseEntity {

    private String name;
    private String email;
    private String phone;
    private String resumeUrl;
    private String resumeText;
    private List<String> tags;
    private String notes;
    private String passwordHash;

    public Candidate(UUID id, String name, String email) {
        super(id);
        setName(name);
        setEmail(email);
        this.tags = new ArrayList<>();
    }

    public void setPassword(String encodedPassword) {
        this.passwordHash = encodedPassword;
    }

    public boolean hasPassword() { return passwordHash != null; }
    public String getPasswordHash() { return passwordHash; }

    public void updateResume(String resumeUrl, String resumeText) {
        this.resumeUrl = resumeUrl;
        this.resumeText = resumeText;
        markUpdated();
    }

    public void addTag(String tag) {
        if (!tags.contains(tag)) { tags.add(tag); markUpdated(); }
    }

    public void addNote(String note) {
        this.notes = (this.notes == null ? "" : this.notes + "\n") + note;
        markUpdated();
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getResumeUrl() { return resumeUrl; }
    public String getResumeText() { return resumeText; }
    public List<String> getTags() { return Collections.unmodifiableList(tags); }
    public String getNotes() { return notes; }

    public void setPhone(String phone) { this.phone = phone; }
    public void setName(String name) {
        if (name == null || name.trim().length() < 2) throw new IllegalArgumentException("Name must be at least 2 characters");
        this.name = name.trim();
    }
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email");
        this.email = email.toLowerCase().trim();
    }
}
