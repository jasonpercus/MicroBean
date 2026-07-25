package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import jakarta.servlet.http.HttpServletRequest;

public interface WebRequest {

    String path();

    String method();

    String header(String name);

    String parameter(String name);

    HttpServletRequest servletRequest();
}
