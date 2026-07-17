package com.jasonpercus.microbean.service;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.UUID;
import com.jasonpercus.microbean.api.Service;
import com.jasonpercus.microbean.api.server.HttpRequestsListener;
import com.jasonpercus.microbean.api.server.HttpServer;
import com.jasonpercus.microbean.api.server.exception.HttpServerException;
import com.jasonpercus.microbean.infrastructure.helpers.LogHelper;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

@Service
public class TomcatHttpServer implements HttpServer {

    private HttpRequestsListener httpRequestsListener;
    private String hostname;
    private int port;
    private boolean logRequests;
    private Tomcat tomcat;
    private Context context;

    @Override
    public void initialize(String hostname, int port, boolean logRequests, HttpRequestsListener httpRequestsListener) {
        this.hostname = getHostname(hostname);
        this.port = getPort(port);
        this.logRequests = logRequests;
        this.httpRequestsListener = httpRequestsListener;

        tomcat = new Tomcat();

        Connector connector = new Connector();
        connector.setProperty("address", this.hostname);
        connector.setPort(this.port);

        tomcat.getService().addConnector(connector);
        context = tomcat.addContext("", null);
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
    public void registerServlet(String path, Servlet servlet) {

        Wrapper wrapper = Tomcat.addServlet(context, servlet.getClass().getSimpleName() + "-" + UUID.randomUUID(), servlet);

        wrapper.setLoadOnStartup(1);

        context.addServletMappingDecoded(path, wrapper.getName());
    }

    @Override
    public void registerFilter(String path, Filter filter) {

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
