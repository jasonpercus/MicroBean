package com.jasonpercus.microbean.component.resolver;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.reflect.Parameter;
import com.jasonpercus.microbean.api.WebExtension;
import com.jasonpercus.microbean.api.WebPathParam;
import com.jasonpercus.microbean.component.converter.WebParameterConverter;
import com.jasonpercus.microbean.infrastructure.model.WebInvocationContext;

@WebExtension(name = "MicroBean")
public class PathParamArgumentResolver implements ArgumentResolver {

    private final WebParameterConverter converter;

    public PathParamArgumentResolver(WebParameterConverter converter) {
        this.converter = converter;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(WebPathParam.class);
    }

    @Override
    public Object resolve(Parameter parameter, WebInvocationContext context) {

        WebPathParam annotation = parameter.getAnnotation(WebPathParam.class);

        String value = context.pathVariables()
                .get(annotation.value());

        return converter.convert(
                value,
                parameter.getType()
        );
    }
}
