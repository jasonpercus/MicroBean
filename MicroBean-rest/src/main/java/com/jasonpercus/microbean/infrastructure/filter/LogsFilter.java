package com.jasonpercus.microbean.infrastructure.filter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import java.util.UUID;
import com.jasonpercus.microbean.api.HttpRequestsListener;
import com.jasonpercus.microbean.infrastructure.HttpRequestContext;
import com.jasonpercus.microbean.infrastructure.HttpResponseContext;
import com.jasonpercus.microbean.infrastructure.RequestContextHolder;
import com.jasonpercus.microbean.infrastructure.api.ModeLog;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LogsFilter implements Filter {

    private final HttpRequestsListener listener;
    private final ModeLog modeLog;

    public LogsFilter(HttpRequestsListener listener, ModeLog modeLog) {
        this.listener = listener;
        this.modeLog = modeLog;
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
            if (modeLog == ModeLog.REQUEST || modeLog == ModeLog.BOTH)
                listener.onRequestStart(correlationId, requestContext);

            filterChain.doFilter(servletRequest, servletResponse);

        } finally {

            long duration = System.currentTimeMillis() - start;

            if (modeLog == ModeLog.RESPONSE || modeLog == ModeLog.BOTH)
                listener.onRequestEnd(correlationId, requestContext, responseContext, duration);
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
