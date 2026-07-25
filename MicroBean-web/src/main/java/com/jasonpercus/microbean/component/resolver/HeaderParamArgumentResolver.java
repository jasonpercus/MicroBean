package com.jasonpercus.microbean.component.resolver;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.reflect.Parameter;
import com.jasonpercus.microbean.api.WebExtension;
import com.jasonpercus.microbean.api.WebHeaderParam;
import com.jasonpercus.microbean.component.converter.WebParameterConverter;
import com.jasonpercus.microbean.infrastructure.model.WebInvocationContext;

@WebExtension(name = "MicroBean")
public class HeaderParamArgumentResolver implements ArgumentResolver {

    private final WebParameterConverter converter;

    public HeaderParamArgumentResolver(WebParameterConverter converter) {
        this.converter = converter;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(WebHeaderParam.class);
    }

    @Override
    public Object resolve(Parameter parameter, WebInvocationContext context) {

        WebHeaderParam annotation = parameter.getAnnotation(WebHeaderParam.class);

        String value = context.request().getHeader(annotation.value());

        return converter.convert(value, parameter.getType());
    }
}
