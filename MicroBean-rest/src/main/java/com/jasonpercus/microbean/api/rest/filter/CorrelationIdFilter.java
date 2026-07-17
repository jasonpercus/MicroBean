package com.jasonpercus.microbean.api.rest.filter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import java.util.UUID;
import com.jasonpercus.microbean.api.server.context.DefaultRequestContext;
import com.jasonpercus.microbean.api.server.context.RequestContextHolder;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class CorrelationIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        try {
            RequestContextHolder.set(new DefaultRequestContext(UUID.randomUUID()));

            filterChain.doFilter(servletRequest, servletResponse);

        } finally {
            RequestContextHolder.clear();
        }
    }
}
