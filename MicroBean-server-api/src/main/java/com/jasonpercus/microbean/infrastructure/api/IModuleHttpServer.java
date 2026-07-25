package com.jasonpercus.microbean.infrastructure.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public interface IModuleHttpServer {

    String KEY_CONFIG_SERVER_HOST = "server.host";
    String KEY_CONFIG_SERVER_PORT = "server.port";
    String KEY_CONFIG_SERVER_PATH = "path";
    String DEFAULT_ROOT_PATH_TO_SERVLETS_OR_FILTERS = "/*";
    String DEFAULT_REST_PATH = "/api";
    String DEFAULT_SWAGGER_UI_PATH = "/swagger-ui";
    String DEFAULT_WEB_PATH = "/";
    String DEFAULT_WEB_ROOT = "classpath:/web";
    String BASE_URL_HTTP = "http://%s:%d";
    String TEMPLATE_KEYS_VALUES_CONFIG_HTTP_MODULE = "%s.%s.%s";

    @SuppressWarnings("unchecked")
    default String moduleName(String className) {
        try {
            Class<?> clazz = Class.forName(className);

            if (IModuleHttpServer.class.isAssignableFrom(clazz))
                return moduleName((Class<? extends IModuleHttpServer>) clazz);
            else
                return null;

        } catch (Exception e) {
            return null;
        }
    }

    default String moduleName(Class<? extends IModuleHttpServer> moduleClass) {
        return moduleClass.getAnnotation(ModuleHttpServer.class).name();
    }

    default String moduleName() {
        return moduleName(this.getClass());
    }

    void initialize();

    boolean enabled();

    String provider();

    String host();

    int port();

    String path();

    void registerServlets(IHttpServer server);

    void registerFilters(IHttpServer server);
}
