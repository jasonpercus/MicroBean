package com.jasonpercus.microbean.entrypoint;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.ERROR_STARTING_HTTP_SERVER;
import static com.jasonpercus.microbean.infrastructure.Constants.PORT_IS_ALREADY_USED_BY_ANOTHER_HTTP_SERVER;
import static com.jasonpercus.microbean.infrastructure.Constants.STARTING_HTTP_SERVER_FOR_PROVIDER;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.LifecycleEntryPoint;
import com.jasonpercus.microbean.infrastructure.ServerInstance;
import com.jasonpercus.microbean.infrastructure.api.HttpServer;
import com.jasonpercus.microbean.infrastructure.api.IHttpServer;
import com.jasonpercus.microbean.infrastructure.api.IModuleHttpServer;
import com.jasonpercus.microbean.infrastructure.api.ModuleHttpServer;
import com.jasonpercus.microbean.infrastructure.exception.MicroBeanWebServerException;
import com.jasonpercus.microbean.infrastructure.helpers.LogHelper;

@EntryPointService(lifecycle = LifecycleEntryPoint.LONG_RUNNING)
public class HttpServerEntryPoint implements ApplicationEntryPoint {

    @Override
    public void main(String[] args) throws Exception {

        List<Object> modulesObj = MicroBean.getContext().getBeansByAnnotation(ModuleHttpServer.class);

        List<ServerInstance> serverInstances = new ArrayList<>();

        for (Object moduleObj : modulesObj)
            if (moduleObj instanceof IModuleHttpServer module)
                initModule(serverInstances, module);

        for (ServerInstance serverInstance : serverInstances) {
            String provider = serverInstance.getProvider();

            IHttpServer server;

            if (provider == null || provider.trim().isBlank()) {
                List<Class<?>> clazz = MicroBean.getContext().getBeanTypesByAnnotation(HttpServer.class);
                if (clazz.isEmpty())
                    throw new MicroBeanWebServerException("No HTTP server provider found for modules: [%s]"
                            .formatted(serverInstance.getModules().stream()
                                    .map(IModuleHttpServer::getClass)
                                    .map(Class::getName)
                                    .toList()
                            ));
                else
                    server = MicroBean.getContext().getBean(clazz.get(0));
            } else
                server = MicroBean.getContext().getBean(IHttpServer.class, provider);

            List<String> contextPaths = serverInstance.getModules().stream()
                    .map(IModuleHttpServer::path)
                    .filter(Objects::nonNull)
                    .sorted(getSortPathComparator())
                    .toList();

            server.initialize(serverInstance.getHost(), serverInstance.getPort(), contextPaths);

            serverInstance.getModules().forEach(module -> {
                module.registerServlets(server);
            });

            serverInstance.getModules().forEach(module -> {
                module.registerFilters(server);
            });

            new Thread(() -> {
                try {
                    server.start();
                } catch (MicroBeanWebServerException e) {
                    LogHelper.error(ERROR_STARTING_HTTP_SERVER.formatted(e.getMessage()), e);
                }
            }).start();
        }
    }

    private static Comparator<String> getSortPathComparator() {
        return (o1, o2) -> {
            if (o1.length() < o2.length())
                return -1;
            else if (o1.length() > o2.length())
                return 1;
            else
                return o1.compareTo(o2);
        };
    }

    private static void initModule(List<ServerInstance> serverInstances, IModuleHttpServer module) {

        module.initialize();

        String moduleName = module.moduleName();

        if (!module.enabled()) {
            LogHelper.trace("Http module '%s' is disabled, skipping server start.", moduleName);
            return;
        }

        String provider = module.provider();
        String host = module.host();
        int port = module.port();
        String path = module.path();

        ServerInstance serverInstance = new ServerInstance(provider, host, port);

        ServerInstance existingInstance = serverInstances.stream()
                .filter(instance -> instance.equals(serverInstance))
                .findFirst()
                .orElse(null);

        if (existingInstance == null) {

            boolean samePortUsed = serverInstances.stream()
                    .map(ServerInstance::getPort)
                    .anyMatch(p -> p == port);

            if (samePortUsed)
                throw new MicroBeanWebServerException(PORT_IS_ALREADY_USED_BY_ANOTHER_HTTP_SERVER, port, moduleName);

            serverInstance.getModules().add(module);
            serverInstances.add(serverInstance);
        } else {
            existingInstance.getModules().add(module);
        }

        LogHelper.debug(STARTING_HTTP_SERVER_FOR_PROVIDER, moduleName, provider, host, port, path);
    }
}
