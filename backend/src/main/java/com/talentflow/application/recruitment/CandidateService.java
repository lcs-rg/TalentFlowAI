package com.talentflow.application.recruitment;

import com.talentflow.domain.recruitment.*;
import com.talentflow.infrastructure.security.TenantContext;
import com.talentflow.presentation.dto.request.*;
import com.talentflow.presentation.dto.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CandidateService {

    private static final Logger log = LoggerFactory.getLogger(CandidateService.class);
    private final CandidateRepository candidateRepo;
    private final ApplicationRepository applicationRepo;

    public CandidateService(CandidateRepository cr, ApplicationRepository ar) {
        this.candidateRepo = cr;
        this.applicationRepo = ar;
    }

    @Transactional
    public CandidateResponse create(CreateCandidateRequest req) {
        Candidate c = new Candidate(UUID.randomUUID(), req.name(), req.email());
        if (req.phone() != null && !req.phone().isBlank()) c.setPhone(req.phone());
        c = candidateRepo.save(c);
        log.info("Candidate created: id={}", c.getId());
        return toResponse(c, List.of());
    }

    public Page<CandidateResponse> list(CandidateFilterParams f, Pageable pageable) {
        UUID companyId = TenantContext.requireCompany();
        return candidateRepo.search(f.search(), pageable)
                .map(c -> toResponse(c, applicationRepo.findByCandidate(c.getId(), Pageable.unpaged()).getContent()));
    }

    public CandidateResponse get(UUID id) {
        Candidate c = findActive(id);
        List<Application> apps = applicationRepo.findByCandidate(c.getId(), Pageable.unpaged()).getContent();
        return toResponse(c, apps);
    }

    @Transactional
    public CandidateResponse update(UUID id, CreateCandidateRequest req) {
        Candidate c = findActive(id);
        c.setName(req.name());
        c.setEmail(req.email());
        if (req.phone() != null && !req.phone().isBlank()) c.setPhone(req.phone());
        c = candidateRepo.save(c);
        List<Application> apps = applicationRepo.findByCandidate(c.getId(), Pageable.unpaged()).getContent();
        return toResponse(c, apps);
    }

    @Transactional
    public void softDelete(UUID id) {
        Candidate c = findActive(id);
        candidateRepo.softDelete(id, TenantContext.getUser(), java.time.Instant.now());
        log.info("Candidate soft-deleted: id={}", id);
    }

    // --- helpers ---

    private Candidate findActive(UUID id) {
        return candidateRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Candidato não encontrado"));
    }

    private CandidateResponse toResponse(Candidate c, List<Application> apps) {
        List<CandidateResponse.ApplicationSummary> appSummaries = apps.stream()
                .map(a -> new CandidateResponse.ApplicationSummary(a.getId(), a.getJobId(), a.getStatus().name(), a.getStageId()))
                .toList();
        return new CandidateResponse(c.getId(), c.getName(), c.getEmail(), c.getPhone(),
                c.getResumeUrl(), c.getResumeText(), c.getTags(), c.getNotes(),
                c.getCreatedAt(), c.getUpdatedAt(), appSummaries);
    }
}
