package com.talentflow.infrastructure.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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
                // Candidate token — no company context
                UUID candidateId = UUID.fromString(claims.getSubject());
                String email = claims.get("email", String.class);
                TenantContext.setCandidate(candidateId);

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(email, null,
                                List.of(new SimpleGrantedAuthority("ROLE_CANDIDATE"))));
            } else {
                // Company user token
                UUID userId = UUID.fromString(claims.getSubject());
                UUID companyId = UUID.fromString(claims.get("company_id", String.class));
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);

                @SuppressWarnings("unchecked")
                List<String> permissions = claims.get("permissions", List.class);

                TenantContext.setCompany(companyId);
                TenantContext.setUser(userId);
                TenantContext.setRole(role);

                List<SimpleGrantedAuthority> authorities = permissions != null
                        ? permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                        : List.of(new SimpleGrantedAuthority("ROLE_" + role));

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(email, null, authorities));
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
