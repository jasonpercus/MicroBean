package com.jasonpercus.microbean.service;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.fasterxml.jackson.core.util.JacksonFeature;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.ServerPlugin;
import com.jasonpercus.microbean.api.ControllerRest;
import com.jasonpercus.microbean.api.rest.RestExceptionMapper;
import com.jasonpercus.microbean.api.server.HttpServer;
import com.jasonpercus.microbean.api.server.ServerModule;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

@ServerPlugin
public class RestModule implements ServerModule {

    @Override
    public void initialize(HttpServer server) {

        ResourceConfig config = new ResourceConfig();
        MicroBean.getContext().getBeansByAnnotation(ControllerRest.class).forEach(config::register);
        config.register(JacksonFeature.class);
        config.register(RestExceptionMapper.class);

        ServletContainer servlet = new ServletContainer(config);
        server.registerServlet("/*", servlet);
    }
}
