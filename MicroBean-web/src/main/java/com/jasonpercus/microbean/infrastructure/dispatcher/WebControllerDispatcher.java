package com.jasonpercus.microbean.infrastructure.dispatcher;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface WebControllerDispatcher {

    boolean dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
