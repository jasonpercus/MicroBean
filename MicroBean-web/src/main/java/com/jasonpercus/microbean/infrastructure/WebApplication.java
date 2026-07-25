package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.component.handler.WebHandlerRegistry;
import jakarta.servlet.Servlet;

public class WebApplication {

    private final WebHandlerRegistry registry;
    private final WebErrorHandler errorHandler;

    public WebApplication(WebHandlerRegistry registry, WebErrorHandler errorHandler) {
        this.registry = registry;
        this.errorHandler = errorHandler;
    }

    public Servlet servlet() {
        return new WebServlet(registry.handlers(), errorHandler);
    }
}
