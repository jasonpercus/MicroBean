package com.jasonpercus.microbean.infrastructure.model;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.Map;

public record WebRouteMatch(
        boolean matched,
        Map<String, String> variables
) {

    public static WebRouteMatch noMatch() {
        return new WebRouteMatch(false, Map.of());
    }
}
