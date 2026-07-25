package com.jasonpercus.microbean.infrastructure.model;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public record WebInvocationContext(
        HttpServletRequest request,
        HttpServletResponse response,
        Map<String, String> pathVariables
) {

}
