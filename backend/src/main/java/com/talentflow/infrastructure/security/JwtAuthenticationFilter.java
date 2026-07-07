package com.talentflow.infrastructure.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) { this.jwtProvider = jwtProvider; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = jwtProvider.validateToken(token);
            String tokenType = claims.get("type", String.class);

            if ("candidate".equals(tokenType)) {
                UUID candidateId = UUID.fromString(claims.getSubject());
                TenantContext.setCandidate(candidateId);

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                claims.get("email", String.class), null,
                                List.of(new SimpleGrantedAuthority("ROLE_CANDIDATE"))));
            } else {
                UUID userId = UUID.fromString(claims.getSubject());
                Object companyIdRaw = claims.get("company_id");
                UUID companyId = companyIdRaw != null
                        ? UUID.fromString(companyIdRaw.toString()) : null;
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);

                if (companyId != null) TenantContext.setCompany(companyId);
                TenantContext.setUser(userId);
                if (role != null) TenantContext.setRole(role);

                // Extract permissions safely
                List<String> permissions = new ArrayList<>();
                Object permRaw = claims.get("permissions");
                if (permRaw instanceof List<?> list) {
                    for (Object item : list) {
                        if (item != null) permissions.add(item.toString());
                    }
                }

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (role != null) authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                for (String p : permissions) {
                    authorities.add(new SimpleGrantedAuthority(p));
                }

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(email, null, authorities));
            }
        } catch (Exception e) {
            log.warn("JWT validation failed: {} — {}", e.getClass().getSimpleName(), e.getMessage());
            SecurityContextHolder.clearContext();
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
