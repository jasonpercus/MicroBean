package com.jasonpercus.microbean.component.routematcher;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.WebExtension;
import com.jasonpercus.microbean.infrastructure.model.WebRouteMatch;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

@WebExtension(name = "MicroBean")
public class SpringWebRouteMatcher implements WebRouteMatcher {

    private final PathPatternParser parser;

    public SpringWebRouteMatcher() {
        this.parser = new PathPatternParser();
    }

    @Override
    public WebRouteMatch match(String pattern, String path) {

        PathPattern pathPattern = parser.parse(pattern);

        PathContainer container = PathContainer.parsePath(path);

        var match = pathPattern.matchAndExtract(container);

        if (match == null)
            return WebRouteMatch.noMatch();

        return new WebRouteMatch(true, match.getUriVariables());
    }
}
