package com.talentflow.application.auth;

import com.talentflow.domain.auth.RefreshToken;
import com.talentflow.domain.auth.RefreshTokenRepository;
import com.talentflow.domain.company.*;
import com.talentflow.domain.identity.*;
import com.talentflow.domain.tenant.*;
import com.talentflow.infrastructure.security.JwtProvider;
import com.talentflow.presentation.dto.request.*;
import com.talentflow.presentation.dto.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final List<String> ADMIN_PERMISSIONS = List.of(
        "job:create","job:read","job:update","job:delete","job:publish",
        "candidate:create","candidate:read","candidate:update","candidate:delete",
        "application:read","application:move","application:feedback",
        "interview:schedule","interview:read",
        "team:manage","company:manage"
    );

    private final TenantRepository tenantRepo;
    private final UserRepository userRepo;
    private final CompanyRepository companyRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(TenantRepository tr, UserRepository ur, CompanyRepository cr,
                       RefreshTokenRepository rtr, PasswordEncoder pe, JwtProvider jp) {
        this.tenantRepo = tr; this.userRepo = ur; this.companyRepo = cr;
        this.refreshTokenRepo = rtr; this.passwordEncoder = pe; this.jwtProvider = jp;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (tenantRepo.existsBySlug(req.companySlug()))
            throw new IllegalArgumentException("Slug já existe: " + req.companySlug());

        Tenant tenant = tenantRepo.save(new Tenant(req.companySlug(), req.companyName(), TenantPlan.FREE));
        Company company = companyRepo.save(new Company(UUID.randomUUID(), tenant.getId(), req.companyName()));

        User user = new User(UUID.randomUUID(), tenant.getId(), req.email(),
                passwordEncoder.encode(req.password()), req.name(), Role.ADMIN);
        userRepo.save(user);

        String access = jwtProvider.generateAccessToken(user.getId(), tenant.getId(), user.getEmail(), user.getRole().name(), ADMIN_PERMISSIONS);
        String rawRefresh = jwtProvider.generateRawRefreshToken();
        RefreshToken rt = RefreshToken.create(user.getId(), "initial-registration", null, jwtProvider.getRefreshTokenTtl());
        refreshTokenRepo.save(rt);

        log.info("Company registered: slug={}, userId={}", req.companySlug(), user.getId());
        return new AuthResponse(access, rawRefresh, (int)(jwtProvider.getAccessTokenExpiration()/1000), toResponse(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        Tenant tenant;
        User user;

        if (req.tenantSlug() != null && !req.tenantSlug().isBlank()) {
            // Slug provided — direct lookup
            tenant = tenantRepo.findBySlug(req.tenantSlug())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
            user = userRepo.findByEmail(tenant.getId(), req.email())
                    .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));
        } else {
            // No slug — search across all tenants
            List<User> users = userRepo.findByEmailAcrossTenants(req.email());
            if (users.isEmpty()) {
                throw new IllegalArgumentException("Credenciais inválidas");
            }
            if (users.size() > 1) {
                throw new IllegalArgumentException("MULTIPLE_TENANTS: Email existe em múltiplas empresas. Informe o slug.");
            }
            user = users.get(0);
            tenant = tenantRepo.findById(user.getTenantId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
        }

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash()))
            throw new IllegalArgumentException("Credenciais inválidas");

        String rawRefresh = jwtProvider.generateRawRefreshToken();
        RefreshToken rt = RefreshToken.create(user.getId(), req.tenantSlug(), null, jwtProvider.getRefreshTokenTtl());
        refreshTokenRepo.save(rt);

        String access = jwtProvider.generateAccessToken(user.getId(), tenant.getId(), user.getEmail(), user.getRole().name(), ADMIN_PERMISSIONS);
        log.info("Login: userId={}, company={}", user.getId(), tenant.getSlug());
        return new AuthResponse(access, rawRefresh, (int)(jwtProvider.getAccessTokenExpiration()/1000), toResponse(user));
    }

    @Transactional
    public AuthResponse refresh(String rawRefreshToken) {
        String hash = RefreshToken.sha256(rawRefreshToken);
        RefreshToken rt = refreshTokenRepo.findByHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token inválido"));

        if (rt.isExpired()) throw new IllegalArgumentException("Refresh token expirado");
        if (rt.isUsed()) {
            // Reuse detection — possible compromise. Revoke all sessions.
            refreshTokenRepo.deleteByUser(rt.getUserId());
            log.warn("Refresh token reuse detected for userId={}. All sessions revoked.", rt.getUserId());
            throw new IllegalArgumentException("Token reutilizado — todas as sessões foram revogadas");
        }

        rt.markUsed();
        refreshTokenRepo.save(rt);

        User user = userRepo.findById(rt.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String newRaw = jwtProvider.generateRawRefreshToken();
        RefreshToken newRt = RefreshToken.create(user.getId(), rt.getDeviceId(), rt.getDeviceInfo(), jwtProvider.getRefreshTokenTtl());
        refreshTokenRepo.save(newRt);

        String access = jwtProvider.generateAccessToken(user.getId(), user.getTenantId(), user.getEmail(), user.getRole().name(), ADMIN_PERMISSIONS);
        return new AuthResponse(access, newRaw, (int)(jwtProvider.getAccessTokenExpiration()/1000), toResponse(user));
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        String hash = RefreshToken.sha256(rawRefreshToken);
        refreshTokenRepo.findByHash(hash).ifPresent(rt -> refreshTokenRepo.deleteByUserAndDevice(rt.getUserId(), rt.getDeviceId()));
    }

    @Transactional
    public void logoutAll(UUID userId) {
        refreshTokenRepo.deleteByUser(userId);
    }

    public List<Map<String, String>> getSessions(UUID userId) {
        return refreshTokenRepo.findByUser(userId).stream()
                .filter(rt -> !rt.isExpired() && !rt.isUsed())
                .map(rt -> Map.of(
                        "deviceId", rt.getDeviceId() != null ? rt.getDeviceId() : "unknown",
                        "deviceInfo", rt.getDeviceInfo() != null ? rt.getDeviceInfo() : "",
                        "createdAt", rt.getCreatedAt().toString(),
                        "expiresAt", rt.getExpiresAt().toString()
                ))
                .toList();
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getTenantId(), u.getEmail(), u.getName(), u.getRole().name(), u.getAvatarUrl());
    }
}
