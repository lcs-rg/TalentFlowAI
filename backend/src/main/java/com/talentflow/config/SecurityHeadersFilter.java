package com.talentflow.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Adds security HTTP headers to every response.
 * OWASP-recommended headers for API + SPA frontend.
 */
@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // Prevent MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");

        // Prevent clickjacking (API doesn't need frames)
        response.setHeader("X-Frame-Options", "DENY");

        // XSS filter (legacy, but still useful as defense-in-depth)
        response.setHeader("X-XSS-Protection", "0");  // deprecated, set to 0 per modern best practice

        // Referrer policy
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Permissions policy (disable camera/mic/geolocation for API)
        response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");

        // HSTS (only in production over HTTPS)
        // response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        // CSP for API responses (restrictive)
        response.setHeader("Content-Security-Policy",
            "default-src 'none'; frame-ancestors 'none'; form-action 'none'");

        // Cache control for sensitive data
        response.setHeader("Cache-Control", "no-store, max-age=0");

        chain.doFilter(request, response);
    }
}
