package com.jasonpercus.microbean.infrastructure.filter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import java.util.Set;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CorsFilter implements Filter {

    private final Set<String> allowedOrigins;

    public CorsFilter(Set<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String origin = httpRequest.getHeader("Origin");

        if (origin != null && isAllowed(origin)) {

            httpResponse.setHeader(
                    "Access-Control-Allow-Origin",
                    origin
            );

            httpResponse.setHeader(
                    "Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS"
            );

            httpResponse.setHeader(
                    "Access-Control-Allow-Headers",
                    "Content-Type, Authorization"
            );

            httpResponse.setHeader(
                    "Access-Control-Allow-Credentials",
                    "true"
            );
        }

        // Réponse au preflight CORS
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isAllowed(String origin) {
        return allowedOrigins.contains(origin);
    }
}
