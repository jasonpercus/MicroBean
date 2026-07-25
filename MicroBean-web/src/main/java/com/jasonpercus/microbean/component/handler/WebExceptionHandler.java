package com.jasonpercus.microbean.component.handler;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface WebExceptionHandler {

    boolean supports(Throwable throwable);

    void handle(Throwable throwable, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
