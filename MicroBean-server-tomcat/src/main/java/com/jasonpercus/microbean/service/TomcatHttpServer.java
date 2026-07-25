package com.jasonpercus.microbean.service;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.jasonpercus.microbean.infrastructure.api.HttpServer;
import com.jasonpercus.microbean.infrastructure.api.IHttpServer;
import com.jasonpercus.microbean.infrastructure.exception.HttpServerException;
import com.jasonpercus.microbean.infrastructure.helpers.LogHelper;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

@HttpServer(name = "tomcat")
public class TomcatHttpServer implements IHttpServer {

    private String hostname;
    private int port;
    private boolean logRequests;
    private Tomcat tomcat;
    private Map<String, Context> contexts;

    @Override
    public void initialize(String hostname, int port, List<String> contextPaths) {
        this.hostname = getHostname(hostname);
        this.port = getPort(port);
        this.contexts = new java.util.HashMap<>();

        tomcat = new Tomcat();

        configureJmxDomain(tomcat);

        Connector connector = new Connector();
        connector.setProperty("address", this.hostname);
        connector.setPort(this.port);

        tomcat.getService().addConnector(connector);

        addContextPaths(contextPaths);
    }

    @Override
    public void start() throws HttpServerException {
        try {
            LogHelper.info("Starting Tomcat server on %s:%d".formatted(hostname, port));
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new HttpServerException("Failed to start Tomcat server", e);
        }
    }

    @Override
    public void stop() {
        try {
            if (tomcat != null) {
                tomcat.stop();
                tomcat = null;
            }
        } catch (Exception e) {
            throw new HttpServerException("Failed to stop Tomcat server", e);
        }
    }

    @Override
    public void registerServlet(String contextPath, String path, Servlet servlet) {

        Context context = contexts.get(contextPath);

        if (context == null)
            return;

        String servletName = servlet.getClass().getSimpleName() + "-" + UUID.randomUUID();

        Wrapper wrapper = Tomcat.addServlet(context, servletName, servlet);

        wrapper.setLoadOnStartup(1);

        context.addServletMappingDecoded(path, wrapper.getName());
    }

    @Override
    public void registerFilter(String contextPath, String path, Filter filter) {

        Context context = contexts.get(contextPath);

        if (context == null)
            return;

        String filterName = filter.getClass().getName();

        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName(filterName);
        filterDef.setFilter(filter);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterName);
        filterMap.addURLPattern(path);

        context.addFilterDef(filterDef);
        context.addFilterMapBefore(filterMap);
    }

    private void addContextPaths(List<String> contextPaths) {
        for(String contextPath : contextPaths) {

            if (contextPath == null || contextPath.trim().equals("/"))
                contextPath = "";

            Context context = tomcat.addContext(contextPath, null);

            contexts.put(contextPath.isEmpty() ? "/" : contextPath, context);
        }
    }

    private String getHostname(String hostname) {

        if (hostname == null || hostname.isEmpty())
            return "localhost";

        return hostname;
    }

    private int getPort(int port) {

        if (port <= 0 || port > 65535)
            return 8080;

        return port;
    }

    /**
     * Configure un domaine JMX unique pour une instance Tomcat embarquée.
     *
     * <p>Par défaut, les instances Tomcat embarquées utilisent toutes le même
     * domaine JMX ({@code Tomcat}) pour enregistrer leurs composants internes
     * sous forme de MBeans. Lorsque plusieurs instances Tomcat sont exécutées
     * au sein d'une même JVM, cela provoque des conflits d'enregistrement avec
     * des exceptions de type {@link javax.management.InstanceAlreadyExistsException}.</p>
     *
     * <p>Cette méthode attribue donc un domaine JMX unique aux composants
     * principaux de l'instance Tomcat afin de permettre l'exécution simultanée
     * de plusieurs serveurs sans collision de MBeans.</p>
     *
     * @param tomcat instance Tomcat embarquée à configurer
     */
    private static void configureJmxDomain(Tomcat tomcat) {

        String domain = "%s-%s".formatted(TomcatHttpServer.class.getSimpleName(), UUID.randomUUID());

        StandardServer server = (StandardServer) tomcat.getServer();
        server.setDomain(domain);

        StandardService service = (StandardService) server.findServices()[0];
        service.setDomain(domain);

        StandardEngine engine = (StandardEngine) tomcat.getEngine();
        engine.setDomain(domain);
    }
}
