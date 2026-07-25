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
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.ControllerRest;
import com.jasonpercus.microbean.api.Environment;
import com.jasonpercus.microbean.api.OpenApiGroup;
import com.jasonpercus.microbean.infrastructure.OpenApiGenerator;
import com.jasonpercus.microbean.infrastructure.OpenApiGroupResolver;
import com.jasonpercus.microbean.infrastructure.OpenApiRegistry;
import com.jasonpercus.microbean.infrastructure.OpenApiServlet;
import com.jasonpercus.microbean.infrastructure.OpenApiStaticResourceServlet;
import com.jasonpercus.microbean.infrastructure.SwaggerInitializerServlet;
import com.jasonpercus.microbean.infrastructure.api.IHttpServer;
import com.jasonpercus.microbean.infrastructure.api.IModuleHttpServer;
import com.jasonpercus.microbean.infrastructure.api.ModuleHttpServer;
import com.jasonpercus.microbean.infrastructure.model.OpenApiDefinition;
import com.jasonpercus.microbean.infrastructure.model.SwaggerUIConfig;
import com.jasonpercus.microbean.infrastructure.model.SwaggerUIServerConfig;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;

@ModuleHttpServer(name = "swagger-ui")
public class ModuleHttpOpenApiServer implements IModuleHttpServer {

    private static final String DEFAULT_LOCALHOST = LOCALHOST;
    private static final int DEFAULT_PORT = LOCALHOST_PORT;

    private final Environment environment;
    private boolean enabled;
    private String provider;
    private String host;
    private int port;
    private String contextPath;
    private String hostApi;
    private int portApi;
    private String contextPathApi;

    public ModuleHttpOpenApiServer(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void initialize() {

        String moduleNameSwaggerUi = moduleName();
        String moduleNameRest = moduleName(ModuleHttpRestServer.class);

        Map<?, ?> conf = getPropSwaggerUiConfiguration(environment, moduleNameSwaggerUi);

        SwaggerUIConfig swaggerConfig = mapConfigurationToObject(conf);

        this.enabled = getPropEnabled(swaggerConfig);
        this.provider = getPropProvider(swaggerConfig);
        this.host = getPropHost(swaggerConfig);
        this.port = getPropPort(swaggerConfig);
        this.contextPath = getPropContextPath(swaggerConfig);
        this.hostApi = getPropHostApi(environment, moduleNameRest);
        this.portApi = getPropPortApi(environment, moduleNameRest);
        this.contextPathApi = getPropContextPathApi(environment, moduleNameRest);
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

        List<Object> controllers = MicroBean.getContext().getBeansByAnnotation(ControllerRest.class);

        OpenApiGroupResolver resolver = new OpenApiGroupResolver();

        Map<String, List<Object>> groups = resolver.resolve(controllers);

        OpenApiRegistry registry = new OpenApiRegistry();

        for (Map.Entry<String, List<Object>> entry : groups.entrySet()) {
            try {
                OpenApiDefinition definition = getOpenApiDefinition(entry);

                registry.register(definition);

                server.registerServlet(contextPath, definition.path(), new OpenApiServlet(definition));
            } catch (OpenApiConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

        server.registerServlet(contextPath, "/swagger-initializer.js", new SwaggerInitializerServlet(contextPath, registry));

        server.registerServlet(
                contextPath,
                DEFAULT_ROOT_PATH_TO_SERVLETS_OR_FILTERS,
                new OpenApiStaticResourceServlet(contextPath, "META-INF/resources/webjars/swagger-ui/5.32.8")
        );
    }

    @Override
    public void registerFilters(IHttpServer server) {

    }

    private static Map<?, ?> getPropSwaggerUiConfiguration(Environment environment, String moduleName) {
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

    private static SwaggerUIConfig mapConfigurationToObject(Map<?, ?> conf) {
        return new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(conf, SwaggerUIConfig.class);
    }

    private static Boolean getPropEnabled(SwaggerUIConfig swaggerConfig) {
        return Optional.ofNullable(swaggerConfig)
                .map(SwaggerUIConfig::isEnabled)
                .orElse(MicroBean.getContext()
                        .getBeanTypesByAnnotation(ControllerRest.class)
                        .stream()
                        .findAny()
                        .isPresent());
    }

    private static String getPropProvider(SwaggerUIConfig swaggerConfig) {
        return Optional.ofNullable(swaggerConfig)
                .map(SwaggerUIConfig::getServer)
                .map(SwaggerUIServerConfig::getProvider)
                .orElse(null);
    }

    private static String getPropHost(SwaggerUIConfig swaggerConfig) {
        return Optional.ofNullable(swaggerConfig)
                .map(SwaggerUIConfig::getServer)
                .map(SwaggerUIServerConfig::getHost)
                .orElse(DEFAULT_LOCALHOST);
    }

    private static Integer getPropPort(SwaggerUIConfig swaggerConfig) {
        return Optional.ofNullable(swaggerConfig)
                .map(SwaggerUIConfig::getServer)
                .map(SwaggerUIServerConfig::getPort)
                .orElse(DEFAULT_PORT);
    }

    private static String getPropContextPath(SwaggerUIConfig swaggerConfig) {
        return Optional.ofNullable(swaggerConfig)
                .map(SwaggerUIConfig::getPath)
                .filter(p -> !p.isBlank())
                .orElse(DEFAULT_SWAGGER_UI_PATH);
    }

    private static String getPropHostApi(Environment environment, String moduleNameRest) {

        if (moduleNameRest == null)
            return DEFAULT_LOCALHOST;

        String keyServerHost = TEMPLATE_KEYS_VALUES_CONFIG_HTTP_MODULE.formatted(DEFAULT_PROPERTY_NAME, moduleNameRest, KEY_CONFIG_SERVER_HOST);

        return Optional.ofNullable(environment)
                .map(Environment::getFlatProperties)
                .map(properties -> properties.get(keyServerHost))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(p -> !p.isBlank())
                .orElse(DEFAULT_LOCALHOST);
    }

    private static Integer getPropPortApi(Environment environment, String moduleNameRest) {

        if (moduleNameRest == null)
            return DEFAULT_PORT;

        String keyServerPort = TEMPLATE_KEYS_VALUES_CONFIG_HTTP_MODULE.formatted(DEFAULT_PROPERTY_NAME, moduleNameRest, KEY_CONFIG_SERVER_PORT);

        return Optional.ofNullable(environment)
                .map(Environment::getFlatProperties)
                .map(properties -> properties.get(keyServerPort))
                .filter(Integer.class::isInstance)
                .map(Integer.class::cast)
                .orElse(DEFAULT_PORT);
    }

    private static String getPropContextPathApi(Environment environment, String moduleNameRest) {

        if (moduleNameRest == null)
            return DEFAULT_REST_PATH;

        String keyServerPath = TEMPLATE_KEYS_VALUES_CONFIG_HTTP_MODULE.formatted(DEFAULT_PROPERTY_NAME, moduleNameRest, KEY_CONFIG_SERVER_PATH);

        return Optional.ofNullable(environment)
                .map(Environment::getFlatProperties)
                .map(properties -> properties.get(keyServerPath))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(p -> !p.isBlank())
                .orElse(DEFAULT_REST_PATH);
    }

    private OpenApiDefinition getOpenApiDefinition(Map.Entry<String, List<Object>> entry) throws OpenApiConfigurationException {

        String path = entry.getKey();
        List<Object> groupControllers = entry.getValue();

        String name = path.equals("/openapi.json")
                ? "Main API"
                : path;

        for (Object controller : groupControllers) {

            OpenApiGroup annotation = controller.getClass().getAnnotation(OpenApiGroup.class);

            if (annotation != null) {
                name = annotation.value();
                break;
            }
        }

        String urlApi = null;
        if (!host.equals(hostApi) || port != portApi)
            urlApi = String.format(BASE_URL_HTTP + "%s", hostApi, portApi, contextPathApi);
        else if (!contextPath.equals(contextPathApi))
            urlApi = contextPathApi;

        OpenApiGenerator generator = new OpenApiGenerator(urlApi, groupControllers);

        return new OpenApiDefinition(name, path, generator.generate());
    }
}
