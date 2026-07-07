package com.talentflow.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Injects API version header into every response.
 * Enables clients to adapt to API changes without breaking.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class ApiVersionFilter implements Filter {

    private static final String API_VERSION = "1.0.0";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("X-API-Version", API_VERSION);

        chain.doFilter(request, response);
    }
}
