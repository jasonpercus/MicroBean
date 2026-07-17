package com.jasonpercus.microbean.service;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.List;
import java.util.Map;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.ControllerRest;
import com.jasonpercus.microbean.openapi.OpenApiDefinition;
import com.jasonpercus.microbean.api.OpenApiGroup;
import com.jasonpercus.microbean.openapi.OpenApiGroupResolver;
import com.jasonpercus.microbean.openapi.OpenApiRegistry;
import com.jasonpercus.microbean.api.ServerPlugin;
import com.jasonpercus.microbean.api.server.HttpServer;
import com.jasonpercus.microbean.api.server.ServerModule;
import com.jasonpercus.microbean.openapi.OpenApiGenerator;
import com.jasonpercus.microbean.openapi.OpenApiServlet;
import com.jasonpercus.microbean.openapi.StaticResourceServlet;
import com.jasonpercus.microbean.openapi.SwaggerInitializerServlet;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;

@ServerPlugin
public class OpenApiModule implements ServerModule {

    @Override
    public void initialize(HttpServer server) {

        List<Object> controllers = MicroBean.getContext().getBeansByAnnotation(ControllerRest.class);

        OpenApiGroupResolver resolver = new OpenApiGroupResolver();

        Map<String, List<Object>> groups = resolver.resolve(controllers);

        OpenApiRegistry registry = new OpenApiRegistry();

        for (Map.Entry<String, List<Object>> entry : groups.entrySet()) {
            try {
                OpenApiDefinition definition = getOpenApiDefinition(entry);

                registry.register(definition);

                server.registerServlet(
                        definition.path(),
                        new OpenApiServlet(definition)
                );
            } catch (OpenApiConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

        server.registerServlet("/swagger/swagger-initializer.js", new SwaggerInitializerServlet(registry));

        server.registerServlet(
                "/swagger/*",
                new StaticResourceServlet("swagger-ui", "META-INF/resources/webjars/swagger-ui/5.32.8")
        );
    }

    private static OpenApiDefinition getOpenApiDefinition(
            Map.Entry<String, List<Object>> entry)
            throws OpenApiConfigurationException {

        String path = entry.getKey();
        List<Object> groupControllers = entry.getValue();

        String name = path.equals("/openapi.json")
                ? "Main API"
                : path;

        for (Object controller : groupControllers) {

            OpenApiGroup annotation =
                    controller.getClass()
                            .getAnnotation(OpenApiGroup.class);

            if (annotation != null) {
                name = annotation.name();
                break;
            }
        }

        OpenApiGenerator generator =
                new OpenApiGenerator(groupControllers);

        return new OpenApiDefinition(
                name,
                "/swagger" + path,
                generator.generate()
        );
    }
}
