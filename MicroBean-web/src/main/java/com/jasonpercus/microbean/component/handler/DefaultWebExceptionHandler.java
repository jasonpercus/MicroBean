package com.jasonpercus.microbean.component.handler;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DefaultWebExceptionHandler implements WebExceptionHandler {

    @Override
    public boolean supports(Throwable throwable) {
        return true;
    }

    @Override
    public void handle(Throwable throwable, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("Internal Server Error");
    }
}
