package com.jasonpercus.microbean.openapi;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OpenApiServlet extends HttpServlet {

    private final ObjectMapper mapper;
    private final OpenApiDefinition definition;

    public OpenApiServlet(OpenApiDefinition definition) {
        this.definition = definition;
        this.mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        OpenAPI openAPI = definition.document();

        mapper.writeValue(
                response.getWriter(),
                openAPI
        );
    }
}
