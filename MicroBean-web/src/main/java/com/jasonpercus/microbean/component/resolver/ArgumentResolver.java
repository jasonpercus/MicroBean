package com.jasonpercus.microbean.component.resolver;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.reflect.Parameter;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.component.converter.PoolWebParameterConverter;
import com.jasonpercus.microbean.component.converter.WebParameterConverter;
import com.jasonpercus.microbean.infrastructure.factory.Context;
import com.jasonpercus.microbean.infrastructure.model.WebInvocationContext;

public interface ArgumentResolver {

    boolean supports(Parameter parameter);

    Object resolve(Parameter parameter, WebInvocationContext context) throws Exception;

    static WebParameterConverter getWebParameterConverter() {

        Context context = MicroBean.getContext();

        PoolWebParameterConverter converter = context.getBeanSilently(PoolWebParameterConverter.class);

        if (converter == null)
            context.registerSingleton(PoolWebParameterConverter.class, converter = new PoolWebParameterConverter());

        return converter;
    }
}
