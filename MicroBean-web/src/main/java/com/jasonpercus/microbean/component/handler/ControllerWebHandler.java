package com.jasonpercus.microbean.component.handler;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.infrastructure.dispatcher.WebControllerDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ControllerWebHandler implements OrderedWebHandler {

    private final WebControllerDispatcher dispatcher;

    public ControllerWebHandler(WebControllerDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public int order() {
        return 10;
    }

    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return dispatcher.dispatch(request, response);
    }
}
