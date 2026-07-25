package com.jasonpercus.microbean.infrastructure.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.List;
import com.jasonpercus.microbean.infrastructure.exception.HttpServerException;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;

public interface IHttpServer {

    void initialize(String hostname, int port, List<String> contextPaths);

    void start() throws HttpServerException;

    void stop() throws HttpServerException;

    void registerServlet(String contextPath, String path, Servlet servlet);

    void registerFilter(String contextPath, String path, Filter filter);
}
