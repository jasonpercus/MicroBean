package com.jasonpercus.microbean.component.routematcher;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.infrastructure.model.WebRouteMatch;

public interface WebRouteMatcher {

    WebRouteMatch match(String pattern, String path);

}
