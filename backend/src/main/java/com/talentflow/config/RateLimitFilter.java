package com.talentflow.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory rate limiter.
 * Limits:
 *   - /auth/login     → 5 requests per minute per IP
 *   - /auth/register  → 3 requests per minute per IP
 *   - /ai/            → 10 requests per minute per IP (token bucket)
 *   - /api/**         → 100 requests per minute per IP (general)
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private record Bucket(int capacity, int refillPerMinute, Map<String, Integer> tokens, Map<String, Instant> lastRefill) {}

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitFilter() {
        buckets.put("/api/v1/auth/login", new Bucket(5, 5, new ConcurrentHashMap<>(), new ConcurrentHashMap<>()));
        buckets.put("/api/v1/auth/register", new Bucket(3, 3, new ConcurrentHashMap<>(), new ConcurrentHashMap<>()));
        buckets.put("/api/v1/ai/", new Bucket(10, 10, new ConcurrentHashMap<>(), new ConcurrentHashMap<>()));
        buckets.put("/api/v1/", new Bucket(100, 100, new ConcurrentHashMap<>(), new ConcurrentHashMap<>()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String ip = getClientIp(request);

        // Find matching bucket
        var opt = buckets.entrySet().stream()
                .filter(e -> path.startsWith(e.getKey()))
                .findFirst();

        if (opt.isPresent()) {
            var entry = opt.get();
            Bucket bucket = entry.getValue();
            String key = ip + ":" + entry.getKey();

            // Refill tokens
            Instant now = Instant.now();
            Instant last = bucket.lastRefill.getOrDefault(key, now);
            if (last.plusSeconds(60).isBefore(now)) {
                bucket.tokens.put(key, bucket.capacity);
                bucket.lastRefill.put(key, now);
            }

            int available = bucket.tokens.getOrDefault(key, bucket.capacity);
            if (available <= 0) {
                log.warn("Rate limit exceeded: ip={}, path={}", ip, path);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"success\":false,\"error\":{\"status\":429,\"code\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Muitas requisições. Tente novamente em instantes.\"},\"timestamp\":\"" + Instant.now() + "\"}");
                response.setHeader("X-RateLimit-Limit", String.valueOf(bucket.capacity));
                response.setHeader("X-RateLimit-Remaining", "0");
                response.setHeader("Retry-After", "60");
                return;
            }

            bucket.tokens.put(key, available - 1);
            response.setHeader("X-RateLimit-Limit", String.valueOf(bucket.capacity));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(available - 1));
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
