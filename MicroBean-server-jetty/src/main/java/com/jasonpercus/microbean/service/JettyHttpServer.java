package com.jasonpercus.microbean.service;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.EnumSet;
import com.jasonpercus.microbean.api.Service;
import com.jasonpercus.microbean.api.server.HttpServer;
import com.jasonpercus.microbean.api.server.HttpRequestsListener;
import com.jasonpercus.microbean.api.server.exception.HttpServerException;
import com.jasonpercus.microbean.infrastructure.helpers.LogHelper;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

@Service
public class JettyHttpServer implements HttpServer {

    private Server server;
    private ServletContextHandler context;
    private String hostname;
    private int port;

    @Override
    public void initialize(String hostname, int port, boolean logRequests, HttpRequestsListener httpRequestsListener) {
        this.hostname = getHostname(hostname);
        this.port = getPort(port);

        server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setHost(this.hostname);
        connector.setPort(this.port);

        server.addConnector(connector);
        server.setStopAtShutdown(true);

        context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        server.setHandler(context);
    }

    @Override
    public void start() throws HttpServerException {
        try {
            LogHelper.info("Starting Jetty server on %s:%d".formatted(hostname, port));
            server.start();
            server.join();
        } catch(Exception e) {
            throw new HttpServerException("Failed to start Jetty server", e);
        }
    }

    @Override
    public void stop() throws HttpServerException {
        try {
            if(server != null) {
                server.stop();
                server.destroy();
                server = null;
            }
        } catch(Exception e) {
            throw new HttpServerException("Failed to stop Jetty server", e);
        }
    }

    @Override
    public void registerServlet(String path, Servlet servlet) {

        ServletHolder holder = new ServletHolder(servlet);

        context.addServlet(holder, path);
    }

    @Override
    public void registerFilter(String path, Filter filter) {

        FilterHolder holder = new FilterHolder(filter);

        context.addFilter(holder, path, EnumSet.of(DispatcherType.REQUEST));
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
