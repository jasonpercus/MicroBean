package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import java.io.InputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OpenApiStaticResourceServlet extends HttpServlet {

    private final String customPrefix;
    private final String resourcePrefix;

    public OpenApiStaticResourceServlet(String customPrefix, String resourcePrefix) {
        this.customPrefix = customPrefix;
        this.resourcePrefix = resourcePrefix;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String path = request.getPathInfo();

        if (path == null || path.equals("/"))
            path = "/index.html";

        InputStream input = findResource(path);

        if (input == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);

        try (input) {
            input.transferTo(response.getOutputStream());
        }
    }

    private InputStream findResource(String path) {

        ClassLoader classLoader = getClass().getClassLoader();

        InputStream input = null;

        if (customPrefix != null)
            input = classLoader.getResourceAsStream(customPrefix + path);

        if (input == null)
            input = classLoader.getResourceAsStream(resourcePrefix + path);

        return input;
    }
}
