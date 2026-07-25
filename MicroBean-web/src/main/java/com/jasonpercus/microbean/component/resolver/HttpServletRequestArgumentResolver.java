package com.jasonpercus.microbean.component.resolver;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.reflect.Parameter;
import com.jasonpercus.microbean.api.WebExtension;
import com.jasonpercus.microbean.infrastructure.model.WebInvocationContext;
import jakarta.servlet.http.HttpServletRequest;

@WebExtension(name = "MicroBean")
public class HttpServletRequestArgumentResolver implements ArgumentResolver {

    @Override
    public boolean supports(Parameter parameter) {
        return HttpServletRequest.class.isAssignableFrom(parameter.getType());
    }

    @Override
    public Object resolve(Parameter parameter, WebInvocationContext context) {
        return context.request();
    }
}
