package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public class DefaultWebResponseBuilder implements WebResponseBuilder {

    @Override
    public WebResponse build(Object value) {

        if (value instanceof WebResponse response)
            return response;

        return WebResponse.ok(value);
    }
}
