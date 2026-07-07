package com.talentflow.presentation.api.v1;

import com.talentflow.domain.company.*;
import com.talentflow.infrastructure.security.TenantContext;
import com.talentflow.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {

    private final CompanyRepository companyRepo;

    public CompanyController(CompanyRepository cr) { this.companyRepo = cr; }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> get() {
        UUID tenantId = TenantContext.requireCompany();
        Company c = companyRepo.findByTenantId(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
        return ResponseEntity.ok(ApiResponse.ok(toMap(c)));
    }

    public record UpdateCompanyRequest(
            @NotBlank String name,
            String industry,
            String size,
            String website) {}

    @PutMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(@Valid @RequestBody UpdateCompanyRequest req) {
        UUID tenantId = TenantContext.requireCompany();
        Company c = companyRepo.findByTenantId(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));

        CompanySize size = req.size != null ? CompanySize.valueOf(req.size.toUpperCase()) : null;
        c.updateProfile(req.name, req.industry, size, req.website);
        c = companyRepo.save(c);

        return ResponseEntity.ok(ApiResponse.ok(toMap(c)));
    }

    private Map<String, Object> toMap(Company c) {
        return Map.of(
                "id", c.getId(),
                "name", c.getName(),
                "industry", c.getIndustry() != null ? c.getIndustry() : "",
                "size", c.getSize() != null ? c.getSize().name() : "",
                "website", c.getWebsite() != null ? c.getWebsite() : "",
                "logoUrl", c.getLogoUrl() != null ? c.getLogoUrl() : ""
        );
    }
}
