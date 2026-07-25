package com.jasonpercus.microbean.component.resolver;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.reflect.Parameter;
import com.jasonpercus.microbean.api.WebExtension;
import com.jasonpercus.microbean.api.WebQueryParam;
import com.jasonpercus.microbean.component.converter.WebParameterConverter;
import com.jasonpercus.microbean.infrastructure.model.WebInvocationContext;

@WebExtension(name = "MicroBean")
public class QueryParamArgumentResolver implements ArgumentResolver {

    private final WebParameterConverter converter;

    public QueryParamArgumentResolver(WebParameterConverter converter) {
        this.converter = converter;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(WebQueryParam.class);
    }

    @Override
    public Object resolve(Parameter parameter, WebInvocationContext context) {

        WebQueryParam annotation = parameter.getAnnotation(WebQueryParam.class);

        String value = context.request().getParameter(annotation.value());

        return converter.convert(value, parameter.getType());
    }
}
