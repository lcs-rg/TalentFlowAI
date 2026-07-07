package com.talentflow.application.candidate;

import com.talentflow.domain.recruitment.Candidate;
import com.talentflow.domain.recruitment.CandidateRepository;
import com.talentflow.infrastructure.security.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CandidateAuthService {

    private static final Logger log = LoggerFactory.getLogger(CandidateAuthService.class);
    private final CandidateRepository candidateRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public CandidateAuthService(CandidateRepository cr, PasswordEncoder pe, JwtProvider jp) {
        this.candidateRepo = cr; this.passwordEncoder = pe; this.jwtProvider = jp;
    }

    public record CandidateAuthResponse(String accessToken, int expiresIn, Map<String, Object> candidate) {}

    @Transactional
    public CandidateAuthResponse register(String name, String email, String password) {
        if (candidateRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Candidate c = new Candidate(UUID.randomUUID(), name, email);
        c.setPassword(passwordEncoder.encode(password));
        c = candidateRepo.save(c);

        log.info("Candidate registered: id={}", c.getId());
        String access = jwtProvider.generateCandidateToken(c.getId(), c.getEmail(), c.getName());
        return new CandidateAuthResponse(access, (int)(jwtProvider.getAccessTokenExpiration()/1000), toMap(c));
    }

    public CandidateAuthResponse login(String email, String password) {
        Candidate c = candidateRepo.findByEmailWithPassword(email)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        if (!c.hasPassword() || !passwordEncoder.matches(password, c.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        log.info("Candidate login: id={}", c.getId());
        String access = jwtProvider.generateCandidateToken(c.getId(), c.getEmail(), c.getName());
        return new CandidateAuthResponse(access, (int)(jwtProvider.getAccessTokenExpiration()/1000), toMap(c));
    }

    public Candidate getAuthenticatedCandidate(UUID candidateId) {
        return candidateRepo.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidato não encontrado"));
    }

    private Map<String, Object> toMap(Candidate c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("name", c.getName());
        m.put("email", c.getEmail());
        m.put("phone", c.getPhone());
        m.put("resumeUrl", c.getResumeUrl());
        return m;
    }
}
