package com.jasonpercus.microbean.component;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.DEFAULT_PROPERTY_NAME;
import static com.jasonpercus.microbean.infrastructure.Constants.LOCALHOST;
import static com.jasonpercus.microbean.infrastructure.Constants.LOCALHOST_PORT;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.jasonpercus.microbean.api.Environment;
import com.jasonpercus.microbean.component.resolver.statics.ResourceResolver;
import com.jasonpercus.microbean.infrastructure.WebApplication;
import com.jasonpercus.microbean.infrastructure.api.IHttpServer;
import com.jasonpercus.microbean.infrastructure.api.IModuleHttpServer;
import com.jasonpercus.microbean.infrastructure.api.ModuleHttpServer;
import com.jasonpercus.microbean.infrastructure.dispatcher.DefaultWebControllerDispatcher;
import com.jasonpercus.microbean.component.handler.ControllerWebHandler;
import com.jasonpercus.microbean.component.handler.DefaultWebErrorHandler;
import com.jasonpercus.microbean.component.handler.StaticResourceHandler;
import com.jasonpercus.microbean.component.handler.WebHandlerRegistry;
import com.jasonpercus.microbean.infrastructure.helpers.LogHelper;
import com.jasonpercus.microbean.infrastructure.model.WebConfig;
import com.jasonpercus.microbean.infrastructure.model.WebServerConfig;

@ModuleHttpServer(name = "web")
public class ModuleHttpWebServer implements IModuleHttpServer {

    private static final String DEFAULT_LOCALHOST = LOCALHOST;
    private static final int DEFAULT_PORT = LOCALHOST_PORT;

    private final Environment environment;

    private boolean enabled;
    private String provider;
    private String host;
    private int port;
    private String contextPath;
    private String root;

    public ModuleHttpWebServer(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void initialize() {

        String moduleName = moduleName();

        Map<?, ?> conf = getPropWebConfiguration(environment, moduleName);

        WebConfig webConfig = mapConfigurationToObject(conf);

        this.enabled = getPropEnabled(webConfig);
        this.provider = getPropProvider(webConfig);
        this.host = getPropHost(webConfig);
        this.port = getPropPort(webConfig);
        this.contextPath = getPropContextPath(webConfig);
        this.root = getPropRoot(webConfig);
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public String provider() {
        return provider;
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public String path() {
        return contextPath;
    }

    @Override
    public void registerServlets(IHttpServer server) {
        try {
            ResourceResolver resolver = ResourceResolver.create(root);
            DefaultWebControllerDispatcher dispatcher = new DefaultWebControllerDispatcher();

            WebHandlerRegistry registry = new WebHandlerRegistry(List.of(
                    new StaticResourceHandler(resolver),
                    new ControllerWebHandler(dispatcher)
            ));

            WebApplication application = new WebApplication(registry, new DefaultWebErrorHandler());

            server.registerServlet(contextPath, "/", application.servlet());
        } catch (Exception e) {
            LogHelper.error("Failed to register servlets for ModuleHttpWebServer", e);
        }
    }

    @Override
    public void registerFilters(IHttpServer server) {

    }

    private static Map<?, ?> getPropWebConfiguration(Environment environment, String moduleName) {
        return Optional.ofNullable(environment)
                .map(Environment::getProperties)
                .map(properties -> properties.get(DEFAULT_PROPERTY_NAME))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(microbeanProperties -> microbeanProperties.get(moduleName))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .orElse(null);
    }

    private static WebConfig mapConfigurationToObject(Map<?, ?> conf) {
        return new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(conf, WebConfig.class);
    }

    private static Boolean getPropEnabled(WebConfig swaggerConfig) {
        return Optional.ofNullable(swaggerConfig)
                .map(WebConfig::isEnabled)
                .orElse(true);
    }

    private static String getPropProvider(WebConfig swaggerConfig) {
        return Optional.ofNullable(swaggerConfig)
                .map(WebConfig::getServer)
                .map(WebServerConfig::getProvider)
                .orElse(null);
    }

    private static String getPropHost(WebConfig swaggerConfig) {
        return Optional.ofNullable(swaggerConfig)
                .map(WebConfig::getServer)
                .map(WebServerConfig::getHost)
                .orElse(DEFAULT_LOCALHOST);
    }

    private static Integer getPropPort(WebConfig swaggerConfig) {
        return Optional.ofNullable(swaggerConfig)
                .map(WebConfig::getServer)
                .map(WebServerConfig::getPort)
                .orElse(DEFAULT_PORT);
    }

    private static String getPropContextPath(WebConfig swaggerConfig) {
        return Optional.ofNullable(swaggerConfig)
                .map(WebConfig::getPath)
                .filter(p -> !p.isBlank())
                .orElse(DEFAULT_WEB_PATH);
    }

    private static String getPropRoot(WebConfig swaggerConfig) {
        return Optional.ofNullable(swaggerConfig)
                .map(WebConfig::getRoot)
                .filter(p -> !p.isBlank())
                .orElse(DEFAULT_WEB_ROOT);
    }
}
