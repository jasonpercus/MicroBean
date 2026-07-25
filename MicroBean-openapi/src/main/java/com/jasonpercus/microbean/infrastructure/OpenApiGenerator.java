package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.List;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContext;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

public class OpenApiGenerator {

    private final String urlApi;
    private final List<Object> controllers;

    public OpenApiGenerator(String urlApi, List<Object> controllers) {
        this.urlApi = urlApi;
        this.controllers = controllers;
    }

    public OpenAPI generate() throws OpenApiConfigurationException {

        SwaggerConfiguration configuration = new SwaggerConfiguration();

        if (urlApi != null && !urlApi.trim().equals("/"))
            configuration.openAPI(new OpenAPI().servers(List.of(new Server().url(urlApi))));

        configuration.resourceClasses(
                controllers.stream()
                        .map(Object::getClass)
                        .map(Class::getName)
                        .sorted()
                        .collect(Collectors.toCollection(TreeSet::new))
        );

        JaxrsOpenApiContext<?> context = (JaxrsOpenApiContext<?>) new JaxrsOpenApiContextBuilder<>()
                .ctxId(UUID.randomUUID().toString())
                .openApiConfiguration(configuration)
                .buildContext(true);

        OpenAPI openAPI = context.read();

        openAPI.setInfo(
                new Info()
                        .title("MicroBean API")
                        .version("1.0.0")
        );

        return openAPI;
    }
}
