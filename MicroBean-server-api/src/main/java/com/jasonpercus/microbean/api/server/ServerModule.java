package com.jasonpercus.microbean.api.server;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public interface ServerModule {

    void initialize(HttpServer server);
}
