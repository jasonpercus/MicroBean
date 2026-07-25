package com.jasonpercus.microbean.component.invoker;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.infrastructure.WebRouteMatchResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface WebMethodInvoker {

    Object invoke(WebRouteMatchResult routeMatch, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
