package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.infrastructure.model.WebRouteMatch;

public record WebRouteMatchResult(
        WebRouteDefinition route,
        WebRouteMatch match
) {

}
