package com.jasonpercus.microbean.infrastructure.exception;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public class MicroBeanWebServerException extends MicroBeanException {

    public MicroBeanWebServerException(String message, Object... args) {
        super(message, args);
    }

    public MicroBeanWebServerException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }

    public MicroBeanWebServerException(Throwable cause) {
        super(cause);
    }
}
