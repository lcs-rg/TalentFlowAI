package com.talentflow.presentation.api.v1;

import com.talentflow.application.candidate.CandidateAuthService;
import com.talentflow.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/candidate/auth")
public class CandidateAuthController {

    private final CandidateAuthService authService;

    public CandidateAuthController(CandidateAuthService as) { this.authService = as; }

    public record RegisterRequest(
            @NotBlank @Size(min = 2, max = 255) String name,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, max = 100) String password) {}

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest req) {
        var result = authService.register(req.name(), req.email(), req.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(Map.of(
                "accessToken", result.accessToken(),
                "expiresIn", result.expiresIn(),
                "candidate", result.candidate()
        )));
    }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest req) {
        var result = authService.login(req.email(), req.password());
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "accessToken", result.accessToken(),
                "expiresIn", result.expiresIn(),
                "candidate", result.candidate()
        )));
    }
}
