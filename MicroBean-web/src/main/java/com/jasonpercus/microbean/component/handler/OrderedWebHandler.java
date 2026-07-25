package com.jasonpercus.microbean.component.handler;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public interface OrderedWebHandler extends WebHandler {

    default int order() {
        return 0;
    }
}
