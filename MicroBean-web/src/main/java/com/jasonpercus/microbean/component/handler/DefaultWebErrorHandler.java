package com.jasonpercus.microbean.component.handler;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import com.jasonpercus.microbean.infrastructure.WebErrorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DefaultWebErrorHandler implements WebErrorHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Throwable error) throws IOException {

        if (response.isCommitted())
            return;

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/html;charset=UTF-8");

        response.getWriter().write("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Erreur</title>
            </head>
            <body>
                <h1>Une erreur est survenue</h1>
                <p>Une erreur interne empêche le traitement de la requête.</p>
            </body>
            </html>
            """);
    }
}
