package com.jasonpercus.microbean.component.converter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public interface WebParameterConverter {

    Object convert(String value, Class<?> targetType);

    default IllegalArgumentException getConversionException(Class<?> targetType) {
        return new IllegalArgumentException("Unsupported parameter type: " + targetType);
    }
}
