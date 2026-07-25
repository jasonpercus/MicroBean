package com.jasonpercus.microbean.component.resolver;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.reflect.Parameter;
import com.jasonpercus.microbean.api.WebExtension;
import com.jasonpercus.microbean.infrastructure.DefaultWebRequest;
import com.jasonpercus.microbean.infrastructure.WebRequest;
import com.jasonpercus.microbean.infrastructure.model.WebInvocationContext;

@WebExtension(name = "MicroBean")
public class RequestArgumentResolver implements ArgumentResolver {

    @Override
    public boolean supports(Parameter parameter) {
        return WebRequest.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object resolve(Parameter parameter, WebInvocationContext context) {
        return new DefaultWebRequest(context.request());
    }
}
