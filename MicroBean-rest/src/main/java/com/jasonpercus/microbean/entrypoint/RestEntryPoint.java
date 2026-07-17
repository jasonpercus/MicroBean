package com.jasonpercus.microbean.entrypoint;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.List;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.LifecycleEntryPoint;
import com.jasonpercus.microbean.api.ServerPlugin;
import com.jasonpercus.microbean.api.rest.DefaultHttpRequestsListener;
import com.jasonpercus.microbean.api.rest.MicroBeanRestException;
import com.jasonpercus.microbean.api.rest.filter.CorrelationIdFilter;
import com.jasonpercus.microbean.api.rest.filter.LogsFilter;
import com.jasonpercus.microbean.api.server.ConfigHttpServer;
import com.jasonpercus.microbean.api.server.HttpRequestsListener;
import com.jasonpercus.microbean.api.server.HttpServer;
import com.jasonpercus.microbean.api.server.ServerModule;
import com.jasonpercus.microbean.infrastructure.exception.MicroBeanException;

@EntryPointService(lifecycle = LifecycleEntryPoint.LONG_RUNNING)
public class RestEntryPoint implements ApplicationEntryPoint {

    public static final String THIS_CLASS_NAME = RestEntryPoint.class.getSimpleName();

    @Override
    public void main(String[] args) {

        HttpServer server = getHttpServer();

        ConfigHttpServer configHttpServer = getConfigHttpServer();
        HttpRequestsListener httpRequestsListener = getHttpServerListener();

        server.initialize(configHttpServer.getHostname(), configHttpServer.getPort(), configHttpServer.islogRequests(), httpRequestsListener);

        List<Object> modules = MicroBean.getContext().getBeansByAnnotation(ServerPlugin.class);

        for (Object o : modules) {
            ServerModule module = (ServerModule) o;
            module.initialize(server);
        }

        server.registerFilter("/*", new CorrelationIdFilter());
        if (configHttpServer.islogRequests() && httpRequestsListener != null)
            server.registerFilter("/*", new LogsFilter(httpRequestsListener));

        server.start();
    }

    /**
     * Récupère le bean HttpServer à partir du contexte MicroBean. Si aucun bean n'est trouvé, une exception est levée.
     *
     * @return le bean HttpServer
     * @throws MicroBeanRestException si aucun bean HttpServer n'est trouvé ou si plusieurs beans sont trouvés
     */
    private static HttpServer getHttpServer() {
        try {
            return MicroBean.getContext().getBean(HttpServer.class);
        } catch (MicroBeanException e) {
            try {
                if (e.getMessage().contains("Multiple beans found for type: c.j.m.a.s.HttpServer")) {
                    return MicroBean.getContext().getBean(HttpServer.class, THIS_CLASS_NAME);
                }
            } catch (MicroBeanException ex) {
                throw new MicroBeanRestException(e);
            }
        }
        throw new MicroBeanRestException("No HttpServer bean found for " + THIS_CLASS_NAME);
    }

    /**
     * Récupère le bean ConfigHttpServer à partir du contexte MicroBean. Si aucun bean n'est trouvé, un nouveau ConfigHttpServer est créé.
     *
     * @return le bean ConfigHttpServer
     * @throws MicroBeanRestException si une exception MicroBean est levée lors de la récupération du bean
     */
    private ConfigHttpServer getConfigHttpServer() {
        try {
            return MicroBean.getContext().getBean(ConfigHttpServer.class);
        } catch (MicroBeanException e) {
            if (e.getMessage().contains("bean found for type: com.jasonpercus.microbean.api.server.ConfigHttpServer")) {
                return new ConfigHttpServer();
            } else if (e.getMessage().contains("Multiple beans found for type: c.j.m.a.s.ConfigHttpServer")) {
                return MicroBean.getContext().getBean(ConfigHttpServer.class, THIS_CLASS_NAME);
            } else {
                throw new MicroBeanRestException(e);
            }
        }
    }

    /**
     * Récupère le bean HttpServerListener à partir du contexte MicroBean. Si aucun bean n'est trouvé, un nouveau RestHttpServerListener est créé.
     *
     * @return le bean HttpServerListener
     * @throws MicroBeanRestException si une exception MicroBean est levée lors de la récupération du bean
     */
    private HttpRequestsListener getHttpServerListener() {
        try {
            return MicroBean.getContext().getBean(HttpRequestsListener.class);
        } catch (MicroBeanException e) {
            if (e.getMessage().contains("bean found for type: com.jasonpercus.microbean.api.server." + HttpRequestsListener.class.getSimpleName())) {
                return new DefaultHttpRequestsListener();
            } else if (e.getMessage().contains("Multiple beans found for type: c.j.m.a.s." + HttpRequestsListener.class.getSimpleName())) {
                return MicroBean.getContext().getBean(HttpRequestsListener.class, THIS_CLASS_NAME);
            } else {
                throw new MicroBeanRestException(e);
            }
        }
    }
}
