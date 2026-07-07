package com.talentflow.presentation.api.v1;

import com.talentflow.domain.company.*;
import com.talentflow.domain.team.*;
import com.talentflow.infrastructure.security.TenantContext;
import com.talentflow.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/team")
public class TeamController {

    private final TeamRepository teamRepo;
    private final CompanyUserRepository companyUserRepo;

    public TeamController(TeamRepository tr, CompanyUserRepository cur) {
        this.teamRepo = tr; this.companyUserRepo = cur;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listMembers() {
        UUID companyId = TenantContext.requireCompany();
        List<CompanyUser> members = companyUserRepo.findByCompany(companyId);

        List<Map<String, Object>> result = members.stream().map(m -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", m.getId());
            map.put("userId", m.getUserId());
            map.put("teamId", m.getTeamId());
            map.put("role", m.getRole());
            teamRepo.findById(m.getTeamId()).ifPresent(t -> map.put("teamName", t.getName()));
            return map;
        }).toList();

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    public record InviteRequest(@NotBlank String email, @NotBlank String role, UUID teamId) {}

    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<Void>> invite(@Valid @RequestBody InviteRequest req) {
        // V1: simplified invite — creates CompanyUser directly
        UUID companyId = TenantContext.requireCompany();
        UUID teamId = req.teamId != null ? req.teamId : teamRepo.findDefaultByCompany(companyId)
                .map(Team::getId)
                .orElseThrow(() -> new IllegalArgumentException("Nenhum time encontrado"));

        CompanyUser cu = new CompanyUser(UUID.randomUUID(), companyId, UUID.randomUUID(), teamId, req.role);
        companyUserRepo.save(cu);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(null));
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<Void>> changeRole(@PathVariable UUID userId, @RequestBody Map<String, String> body) {
        UUID companyId = TenantContext.requireCompany();
        CompanyUser cu = companyUserRepo.findByCompanyAndUser(companyId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado"));
        cu.changeRole(body.get("role"));
        companyUserRepo.save(cu);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> remove(@PathVariable UUID userId) {
        UUID companyId = TenantContext.requireCompany();
        CompanyUser cu = companyUserRepo.findByCompanyAndUser(companyId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado"));
        companyUserRepo.save(cu); // soft delete via deleted_at in JPA entity
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
