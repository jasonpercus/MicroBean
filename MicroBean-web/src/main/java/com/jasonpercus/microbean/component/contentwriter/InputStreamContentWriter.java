package com.jasonpercus.microbean.component.contentwriter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.InputStream;
import java.util.List;
import com.jasonpercus.microbean.api.WebExtension;
import jakarta.servlet.http.HttpServletResponse;

@WebExtension(name = "MicroBean")
public class InputStreamContentWriter implements WebContentWriter {

    @Override
    public boolean canWrite(Class<?> bodyType) {
        return InputStream.class.isAssignableFrom(bodyType);
    }

    @Override
    public List<String> contentTypes() {
        return List.of();
    }

    @Override
    public String defaultContentType() {
        return "application/octet-stream";
    }

    @Override
    public void write(Object body, HttpServletResponse response) throws Exception {
        try (InputStream in = (InputStream) body) {
            in.transferTo(response.getOutputStream());
        }
    }
}
