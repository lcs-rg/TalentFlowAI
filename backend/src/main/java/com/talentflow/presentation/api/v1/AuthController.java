package com.talentflow.presentation.api.v1;

import com.talentflow.application.auth.AuthService;
import com.talentflow.infrastructure.security.TenantContext;
import com.talentflow.presentation.dto.ApiResponse;
import com.talentflow.presentation.dto.request.*;
import com.talentflow.presentation.dto.response.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final boolean cookieSecure;

    public AuthController(AuthService authService,
                          @Value("${app.cookie.secure:false}") boolean cookieSecure) {
        this.authService = authService;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req, HttpServletResponse res) {
        AuthResponse data = authService.register(req);
        setRefreshCookie(res, data.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req, HttpServletResponse res) {
        AuthResponse data = authService.login(req);
        setRefreshCookie(res, data.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@CookieValue(name = "refresh_token", required = false) String cookieToken,
                                                              @RequestBody(required = false) RefreshTokenRequest req,
                                                              HttpServletResponse res) {
        String token = cookieToken != null ? cookieToken : (req != null ? req.refreshToken() : null);
        if (token == null) throw new IllegalArgumentException("Refresh token não fornecido");
        AuthResponse data = authService.refresh(token);
        setRefreshCookie(res, data.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@CookieValue(name = "refresh_token", required = false) String token) {
        if (token != null) authService.logout(token);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAll() {
        authService.logoutAll(TenantContext.requireUser());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> sessions() {
        var sessions = authService.getSessions(TenantContext.requireUser());
        return ResponseEntity.ok(ApiResponse.ok(sessions));
    }

    private void setRefreshCookie(HttpServletResponse res, String token) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge((int)(30 * 24 * 60 * 60));
        cookie.setAttribute("SameSite", cookieSecure ? "Strict" : "Lax");
        res.addCookie(cookie);
    }
}
