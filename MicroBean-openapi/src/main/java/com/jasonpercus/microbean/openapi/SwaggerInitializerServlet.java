package com.jasonpercus.microbean.openapi;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SwaggerInitializerServlet extends HttpServlet {

    private final OpenApiRegistry registry;

    public SwaggerInitializerServlet(OpenApiRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        StringBuilder urls = getStringBuilder();

        String codeJs = """
            window.onload = function() {

              window.ui = SwaggerUIBundle({
                urls: %s,
                dom_id: '#swagger-ui',
                deepLinking: true,
                presets: [
                  SwaggerUIBundle.presets.apis,
                  SwaggerUIStandalonePreset
                ],
                plugins: [
                  SwaggerUIBundle.plugins.DownloadUrl
                ],
                layout: "StandaloneLayout"
              });

            };
            """.formatted(urls);


        response.setContentType("application/javascript");
        response.setStatus(HttpServletResponse.SC_OK);

        response.getWriter().write(codeJs);
    }

    private StringBuilder getStringBuilder() {
        StringBuilder urls = new StringBuilder();

        urls.append("[\n");

        for (OpenApiDefinition definition : registry.getDefinitions()) {

            urls.append("""
                {
                  "url": "%s",
                  "name": "%s"
                },
                """.formatted(
                    definition.path(),
                    definition.name()
            ));
        }

        if (!registry.getDefinitions().isEmpty()) {
            urls.setLength(urls.length() - 1); // enlève la dernière virgule
        }

        urls.append("\n]");
        return urls;
    }
}
