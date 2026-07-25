package com.jasonpercus.microbean.service;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import com.jasonpercus.microbean.infrastructure.api.HttpServer;
import com.jasonpercus.microbean.infrastructure.api.IHttpServer;
import com.jasonpercus.microbean.infrastructure.exception.HttpServerException;
import com.jasonpercus.microbean.infrastructure.helpers.LogHelper;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

@HttpServer(name = "jetty")
public class JettyHttpServer implements IHttpServer {

    private Server jetty;
    private Map<String, ServletContextHandler> contexts;
    private String hostname;
    private int port;

    @Override
    public void initialize(String hostname, int port, List<String> contextPaths) {
        this.hostname = getHostname(hostname);
        this.port = getPort(port);
        this.contexts = new java.util.HashMap<>();

        jetty = new Server();
        jetty.setStopAtShutdown(true);

        ServerConnector connector = new ServerConnector(jetty);
        connector.setHost(this.hostname);
        connector.setPort(this.port);

        jetty.addConnector(connector);

        addContextPaths(contextPaths);
    }

    @Override
    public void start() throws HttpServerException {
        try {
            LogHelper.info("Starting Jetty server on %s:%d".formatted(hostname, port));
            jetty.start();
            jetty.join();
        } catch(Exception e) {
            throw new HttpServerException("Failed to start Jetty server", e);
        }
    }

    @Override
    public void stop() throws HttpServerException {
        try {
            if(jetty != null) {
                jetty.stop();
                jetty.destroy();
                jetty = null;
            }
        } catch(Exception e) {
            throw new HttpServerException("Failed to stop Jetty server", e);
        }
    }

    @Override
    public void registerServlet(String contextPath, String path, Servlet servlet) {

        ServletContextHandler context = contexts.get(contextPath);

        if (context == null)
            return;

        ServletHolder holder = new ServletHolder(servlet);

        context.addServlet(holder, path);
    }

    @Override
    public void registerFilter(String contextPath, String path, Filter filter) {

        ServletContextHandler context = contexts.get(contextPath);

        if (context == null)
            return;

        FilterHolder holder = new FilterHolder(filter);

        context.addFilter(holder, path, EnumSet.of(DispatcherType.REQUEST));
    }

    private void addContextPaths(List<String> contextPaths) {

        ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();

        for (String contextPath : contextPaths) {

            if (contextPath == null || contextPath.isBlank())
                contextPath = "/";

            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath(contextPath);

            contextHandlerCollection.addHandler(context);

            contexts.put(contextPath, context);
        }

        jetty.setHandler(contextHandlerCollection);
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
}
