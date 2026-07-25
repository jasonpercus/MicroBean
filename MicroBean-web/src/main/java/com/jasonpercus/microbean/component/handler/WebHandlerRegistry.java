package com.jasonpercus.microbean.component.handler;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.Collection;
import java.util.List;

public class WebHandlerRegistry {

    private final List<WebHandler> handlers;

    public WebHandlerRegistry(Collection<WebHandler> handlers) {
        this.handlers = handlers.stream()
                .sorted(this::compare)
                .toList();
    }

    private int compare(WebHandler a, WebHandler b) {
        return Integer.compare(
                getOrder(a),
                getOrder(b)
        );
    }

    private int getOrder(WebHandler handler) {

        if (handler instanceof OrderedWebHandler ordered)
            return ordered.order();

        return 0;
    }

    public List<WebHandler> handlers() {
        return handlers;
    }
}
