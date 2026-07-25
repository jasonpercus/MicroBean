package com.jasonpercus.microbean.component.contentwriter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.List;
import com.jasonpercus.microbean.api.WebExtension;
import jakarta.servlet.http.HttpServletResponse;

@WebExtension(name = "MicroBean")
public class StringContentWriter implements WebContentWriter {

    @Override
    public boolean canWrite(Class<?> bodyType) {
        return String.class.isAssignableFrom(bodyType);
    }

    @Override
    public List<String> contentTypes() {
        return List.of(
                "text/plain",
                "text/html",
                "text/css",
                "application/javascript"
        );
    }

    @Override
    public String defaultContentType() {
        return "text/plain";
    }

    @Override
    public void write(Object body, HttpServletResponse response) throws Exception {
        response.getWriter().write((String) body);
    }
}
