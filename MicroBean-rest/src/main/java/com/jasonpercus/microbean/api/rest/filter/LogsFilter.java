package com.jasonpercus.microbean.api.rest.filter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import java.util.UUID;
import com.jasonpercus.microbean.api.server.HttpRequestsListener;
import com.jasonpercus.microbean.api.server.context.DefaultRequestContext;
import com.jasonpercus.microbean.api.server.context.RequestContextHolder;
import com.jasonpercus.microbean.api.server.context.http.HttpRequestContext;
import com.jasonpercus.microbean.api.server.context.http.HttpResponseContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LogsFilter implements Filter {

    private final HttpRequestsListener listener;

    public LogsFilter(HttpRequestsListener listener) {
        this.listener = listener;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        HttpRequestContext requestContext = createRequestContext(httpRequest);
        HttpResponseContext responseContext = createResponseContext(httpRequest, httpResponse);

        long start = System.currentTimeMillis();

        UUID correlationId = RequestContextHolder.current().correlationId();

        try {

            RequestContextHolder.set(new DefaultRequestContext(correlationId));

            listener.onRequestStart(correlationId, requestContext);

            filterChain.doFilter(servletRequest, servletResponse);

        } finally {

            long duration = System.currentTimeMillis() - start;

            listener.onRequestEnd(correlationId, requestContext, responseContext, duration);

            RequestContextHolder.clear();
        }
    }

    private HttpRequestContext createRequestContext(HttpServletRequest request) {

        return new HttpRequestContext() {

            @Override
            public ServletRequest getRequest() {
                return request;
            }

            @Override
            public String getMethod() {
                return request.getMethod();
            }

            @Override
            public String getPath() {
                return request.getRequestURI();
            }
        };
    }

    private HttpResponseContext createResponseContext(HttpServletRequest request, HttpServletResponse response) {

        return new HttpResponseContext() {

            @Override
            public ServletRequest getRequest() {
                return request;
            }

            @Override
            public ServletResponse getResponse() {
                return response;
            }

            @Override
            public int getStatus() {
                return response.getStatus();
            }
        };
    }
}
