package com.jasonpercus.microbean.component;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.DEFAULT_PROPERTY_NAME;
import static com.jasonpercus.microbean.infrastructure.Constants.FAILED_TO_INSTANTIATE_HTTP_REQUESTS_LISTENER_CLASS;
import static com.jasonpercus.microbean.infrastructure.Constants.LOCALHOST;
import static com.jasonpercus.microbean.infrastructure.Constants.LOCALHOST_PORT;
import static com.jasonpercus.microbean.infrastructure.api.ModeLog.BOTH;
import static com.jasonpercus.microbean.infrastructure.api.ModeLog.NONE;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.ControllerRest;
import com.jasonpercus.microbean.api.Environment;
import com.jasonpercus.microbean.api.HttpRequestsListener;
import com.jasonpercus.microbean.infrastructure.DefaultHttpRequestsListener;
import com.jasonpercus.microbean.infrastructure.RestExceptionMapper;
import com.jasonpercus.microbean.infrastructure.api.IHttpServer;
import com.jasonpercus.microbean.infrastructure.api.IModuleHttpServer;
import com.jasonpercus.microbean.infrastructure.api.ModeLog;
import com.jasonpercus.microbean.infrastructure.api.ModuleHttpServer;
import com.jasonpercus.microbean.infrastructure.filter.CorrelationIdFilter;
import com.jasonpercus.microbean.infrastructure.filter.CorsFilter;
import com.jasonpercus.microbean.infrastructure.filter.LogsFilter;
import com.jasonpercus.microbean.infrastructure.helpers.LogHelper;
import com.jasonpercus.microbean.infrastructure.model.RestConfig;
import com.jasonpercus.microbean.infrastructure.model.RestServerConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

@ModuleHttpServer(name = "rest")
public class ModuleHttpRestServer implements IModuleHttpServer {

    private static final String CANONICAL_NAME_MODULE_HTTP_OPENAPI_SERVER = "com.jasonpercus.microbean.component.ModuleHttpOpenApiServer";
    private static final String DEFAULT_LOCALHOST = LOCALHOST;
    private static final int DEFAULT_PORT = LOCALHOST_PORT;

    private final Environment environment;
    private boolean enabled;
    private String provider;
    private String host;
    private int port;
    private String contextPath;
    private ModeLog modeLog;
    private String httpRequestsListenerClassname;
    private String moduleNameSwaggerUi;

    public ModuleHttpRestServer(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void initialize() {

        String moduleNameRest = moduleName();
        this.moduleNameSwaggerUi = moduleName(CANONICAL_NAME_MODULE_HTTP_OPENAPI_SERVER);

        Map<?, ?> conf = getPropRestConfiguration(environment, moduleNameRest);

        RestConfig restConfig = mapConfigurationToObject(conf);

        this.enabled = getPropEnabled(restConfig);
        this.provider = getPropProvider(restConfig);
        this.host = getPropLocalhost(restConfig);
        this.port = getPropPort(restConfig);
        this.contextPath = getPropContextPath(restConfig);
        this.modeLog = getPropModeLog(restConfig);
        this.httpRequestsListenerClassname = getPropHttpRequestsListenerClassname(restConfig);
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

        ResourceConfig config = new ResourceConfig();
        config.property(ServerProperties.WADL_FEATURE_DISABLE, true);
        config.register(JacksonFeature.class);
        config.register(RestExceptionMapper.class);
        MicroBean.getContext().getBeansByAnnotation(ControllerRest.class).forEach(config::register);

        ServletContainer servlet = new ServletContainer(config);
        server.registerServlet(contextPath, DEFAULT_ROOT_PATH_TO_SERVLETS_OR_FILTERS, servlet);
    }

    @Override
    public void registerFilters(IHttpServer server) {

        String hostSwagger = getPropLocalhostSwaggerUi(environment, moduleNameSwaggerUi);
        int portSwagger = getPropPortSwaggerSwaggerUi(environment, moduleNameSwaggerUi);

        String urlSwagger = String.format(BASE_URL_HTTP, hostSwagger, portSwagger);

        server.registerFilter(contextPath, DEFAULT_ROOT_PATH_TO_SERVLETS_OR_FILTERS, new CorrelationIdFilter());
        server.registerFilter(contextPath, DEFAULT_ROOT_PATH_TO_SERVLETS_OR_FILTERS, new CorsFilter(Set.of(urlSwagger)));

        if (modeLog != null && modeLog != NONE) {

            try {
                HttpRequestsListener listener;
                if (httpRequestsListenerClassname != null) {
                    Class<?> clazz = Class.forName(httpRequestsListenerClassname);
                    listener = (HttpRequestsListener) clazz.getDeclaredConstructor().newInstance();
                } else {
                    listener = new DefaultHttpRequestsListener();
                }

                server.registerFilter(contextPath,DEFAULT_ROOT_PATH_TO_SERVLETS_OR_FILTERS, new LogsFilter(listener, modeLog));

            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                LogHelper.error(FAILED_TO_INSTANTIATE_HTTP_REQUESTS_LISTENER_CLASS.formatted(httpRequestsListenerClassname), e);
            }
        }
    }

    private static Map<?, ?> getPropRestConfiguration(Environment environment, String moduleName) {
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

    private static RestConfig mapConfigurationToObject(Map<?, ?> conf) {
        return new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(conf, RestConfig.class);
    }

    private static Boolean getPropEnabled(RestConfig restConfig) {
        return Optional.ofNullable(restConfig)
                .map(RestConfig::isEnabled)
                .orElse(MicroBean.getContext()
                        .getBeanTypesByAnnotation(ControllerRest.class)
                        .stream()
                        .findAny()
                        .isPresent());
    }

    private static String getPropProvider(RestConfig restConfig) {
        return Optional.ofNullable(restConfig)
                .map(RestConfig::getServer)
                .map(RestServerConfig::getProvider)
                .orElse(null);
    }

    private static String getPropLocalhost(RestConfig restConfig) {
        return Optional.ofNullable(restConfig)
                .map(RestConfig::getServer)
                .map(RestServerConfig::getHost)
                .orElse(DEFAULT_LOCALHOST);
    }

    private static Integer getPropPort(RestConfig restConfig) {
        return Optional.ofNullable(restConfig)
                .map(RestConfig::getServer)
                .map(RestServerConfig::getPort)
                .orElse(DEFAULT_PORT);
    }

    private static String getPropContextPath(RestConfig restConfig) {
        return Optional.ofNullable(restConfig)
                .map(RestConfig::getPath)
                .filter(p -> !p.isBlank())
                .orElse(DEFAULT_REST_PATH);
    }

    private static ModeLog getPropModeLog(RestConfig restConfig) {
        return Optional.ofNullable(restConfig)
                .map(RestConfig::getModeLog)
                .orElse(BOTH);
    }

    private static String getPropHttpRequestsListenerClassname(RestConfig restConfig) {
        return Optional.ofNullable(restConfig)
                .map(RestConfig::getHttpRequestsListenerClassname)
                .orElse(null);
    }

    private static String getPropLocalhostSwaggerUi(Environment environment, String moduleNameSwaggerUi) {

        if (moduleNameSwaggerUi == null)
            return DEFAULT_LOCALHOST;

        String keyServerHost = TEMPLATE_KEYS_VALUES_CONFIG_HTTP_MODULE.formatted(DEFAULT_PROPERTY_NAME, moduleNameSwaggerUi, KEY_CONFIG_SERVER_HOST);

        return Optional.ofNullable(environment)
                .map(Environment::getFlatProperties)
                .map(properties -> properties.get(keyServerHost))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(p -> !p.isBlank())
                .orElse(DEFAULT_LOCALHOST);
    }

    private static Integer getPropPortSwaggerSwaggerUi(Environment environment, String moduleNameSwaggerUi) {

        if (moduleNameSwaggerUi == null)
            return DEFAULT_PORT;

        String keyServerPort = TEMPLATE_KEYS_VALUES_CONFIG_HTTP_MODULE.formatted(DEFAULT_PROPERTY_NAME, moduleNameSwaggerUi, KEY_CONFIG_SERVER_PORT);

        return Optional.ofNullable(environment)
                .map(Environment::getFlatProperties)
                .map(properties -> properties.get(keyServerPort))
                .filter(Integer.class::isInstance)
                .map(Integer.class::cast)
                .orElse(DEFAULT_PORT);
    }
}
