package com.jasonpercus.microbean.component.converter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.List;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.WebExtension;

public class PoolWebParameterConverter implements WebParameterConverter {

    List<WebParameterConverter> converters;

    public PoolWebParameterConverter() {
        this.converters = MicroBean.getContext().getBeansByAnnotation(WebExtension.class).stream()
                .filter(bean -> bean instanceof WebParameterConverter)
                .map(WebParameterConverter.class::cast)
                .toList();
    }

    @Override
    public Object convert(String value, Class<?> targetType) {

        for (WebParameterConverter converter : converters) {

            Object converted = convert(value, targetType, converter);

            if (converted != null)
                return converted;
        }

        throw getConversionException(targetType);
    }

    private Object convert(String value, Class<?> targetType, WebParameterConverter converter) {
        try {
            return converter.convert(value, targetType);
        } catch (Exception e) {
            return null;
        }
    }
}
