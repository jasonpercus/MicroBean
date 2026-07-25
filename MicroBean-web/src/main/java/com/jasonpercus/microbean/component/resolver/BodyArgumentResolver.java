package com.jasonpercus.microbean.component.resolver;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.reflect.Parameter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonpercus.microbean.api.WebBody;
import com.jasonpercus.microbean.api.WebExtension;
import com.jasonpercus.microbean.infrastructure.model.WebInvocationContext;

@WebExtension(name = "MicroBean")
public class BodyArgumentResolver implements ArgumentResolver {

    private final ObjectMapper mapper;

    public BodyArgumentResolver() {
        this.mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(WebBody.class);
    }

    @Override
    public Object resolve(Parameter parameter, WebInvocationContext context) throws Exception {
        return mapper.readValue(context.request().getInputStream(), parameter.getType());
    }
}
