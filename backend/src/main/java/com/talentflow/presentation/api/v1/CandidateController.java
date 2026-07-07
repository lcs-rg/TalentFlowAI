package com.talentflow.presentation.api.v1;

import com.talentflow.application.recruitment.CandidateService;
import com.talentflow.presentation.dto.ApiResponse;
import com.talentflow.presentation.dto.request.*;
import com.talentflow.presentation.dto.response.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService cs) { this.candidateService = cs; }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> list(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        CandidateFilterParams f = new CandidateFilterParams(null, null, search);
        Page<CandidateResponse> page = candidateService.list(f, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page.getContent(), ApiResponse.Meta.of(page)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CandidateResponse>> create(@Valid @RequestBody CreateCandidateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(candidateService.create(req)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(candidateService.get(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody CreateCandidateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(candidateService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        candidateService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
