package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import java.util.List;
import com.jasonpercus.microbean.component.handler.WebHandler;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class WebServlet extends HttpServlet {

    private final List<WebHandler> handlers;
    private final WebErrorHandler errorHandler;

    public WebServlet(List<WebHandler> handlers, WebErrorHandler errorHandler) {
        this.handlers = handlers;
        this.errorHandler = errorHandler;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            for (WebHandler handler : handlers) {
                if (handler.handle(request, response))
                    return;
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND);

        } catch (Throwable error) {
            errorHandler.handle(request, response, error);
        }
    }
}
