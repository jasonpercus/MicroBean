package com.jasonpercus.microbean.component.handler;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.WebExtension;
import com.jasonpercus.microbean.component.contentwriter.WebContentWriter;
import com.jasonpercus.microbean.component.contentwriter.WebContentWriterRegistry;
import com.jasonpercus.microbean.infrastructure.WebResponse;
import jakarta.servlet.http.HttpServletResponse;

@WebExtension(name = "MicroBean")
public class DefaultWebResponseHandler implements WebResponseHandler {

    private final WebContentWriterRegistry registry;

    public DefaultWebResponseHandler(WebContentWriterRegistry registry) {
        this.registry = registry;
    }

    public void write(WebResponse webResponse, HttpServletResponse response) throws Exception {

        WebContentWriter writer = registry.find(webResponse.body(), webResponse.contentType());

        // Détermination automatique du Content-Type
        if (webResponse.contentType() == null)
            webResponse.contentType(writer.defaultContentType());

        response.setStatus(webResponse.status());
        response.setContentType(webResponse.contentType());

        webResponse.headers().forEach(response::setHeader);

        writer.write(webResponse.body(), response);
    }
}
