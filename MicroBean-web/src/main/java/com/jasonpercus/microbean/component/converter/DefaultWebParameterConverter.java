package com.jasonpercus.microbean.component.converter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.WebExtension;

@WebExtension(name = "MicroBean")
public class DefaultWebParameterConverter implements WebParameterConverter {

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(String value, Class<?> targetType) {

        if (value == null)
            return null;

        if (targetType == String.class)
            return value;

        if (targetType == int.class || targetType == Integer.class)
            return Integer.parseInt(value);

        if (targetType == long.class || targetType == Long.class)
            return Long.parseLong(value);

        if (targetType == boolean.class || targetType == Boolean.class)
            return Boolean.parseBoolean(value);

        if (targetType == double.class || targetType == Double.class)
            return Double.parseDouble(value);

        if (targetType.isEnum())
            return Enum.valueOf((Class<? extends Enum>) targetType, value);

        throw getConversionException(targetType);
    }
}
