package com.jasonpercus.microbean.api.server;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.server.exception.HttpServerException;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;

public interface HttpServer {

    void initialize(String hostname, int port, boolean logRequests, HttpRequestsListener httpRequestsListener);

    void start() throws HttpServerException;

    void stop() throws HttpServerException;

    void registerServlet(String path, Servlet servlet);

    void registerFilter(String path, Filter filter);
}
