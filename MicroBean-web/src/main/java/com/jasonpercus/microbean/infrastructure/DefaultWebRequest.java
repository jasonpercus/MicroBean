package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import jakarta.servlet.http.HttpServletRequest;

public class DefaultWebRequest implements WebRequest {

    private final HttpServletRequest request;

    public DefaultWebRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String path() {
        return request.getRequestURI();
    }

    @Override
    public String method() {
        return request.getMethod();
    }

    @Override
    public String header(String name) {
        return request.getHeader(name);
    }

    @Override
    public String parameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public HttpServletRequest servletRequest() {
        return request;
    }
}
