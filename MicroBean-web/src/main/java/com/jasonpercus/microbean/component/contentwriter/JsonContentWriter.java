package com.jasonpercus.microbean.component.contentwriter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonpercus.microbean.api.WebExtension;
import jakarta.servlet.http.HttpServletResponse;

@WebExtension(name = "MicroBean")
public class JsonContentWriter implements WebContentWriter {

    private final ObjectMapper mapper;

    public JsonContentWriter() {
        this.mapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public boolean canWrite(Class<?> bodyType) {
        return true;
    }

    @Override
    public List<String> contentTypes() {
        return List.of("application/json");
    }

    @Override
    public String defaultContentType() {
        return "application/json";
    }

    @Override
    public void write(Object body, HttpServletResponse response) throws Exception {
        mapper.writeValue(response.getOutputStream(), body);
    }
}
