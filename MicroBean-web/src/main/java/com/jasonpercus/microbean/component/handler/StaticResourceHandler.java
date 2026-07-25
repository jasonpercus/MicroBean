package com.jasonpercus.microbean.component.handler;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import java.io.InputStream;
import com.jasonpercus.microbean.component.resolver.statics.ResourceResolver;
import com.jasonpercus.microbean.infrastructure.WebResource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StaticResourceHandler implements OrderedWebHandler {

    private final ResourceResolver resolver;
    private final String index;

    public StaticResourceHandler(ResourceResolver resolver) {
        this(resolver, "/index.html");
    }

    public StaticResourceHandler(ResourceResolver resolver, String index) {
        this.resolver = resolver;
        this.index = index;
    }

    @Override
    public int order() {
        return 1000;
    }

    public boolean handle(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String path = request.getServletPath();

        if (path == null || path.isEmpty() || "/".equals(path))
            path = this.index;

        WebResource resource = resolver.resolve(path);

        if (resource == null)
            return false;

        response.setContentType(resource.contentType());

        if (resource.contentLength() >= 0)
            response.setContentLengthLong(resource.contentLength());

        try (InputStream input = resource.inputStream()) {
            input.transferTo(response.getOutputStream());
        }

        return true;
    }
}
