package com.jasonpercus.microbean.api.rest;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public class MicroBeanRestException extends RuntimeException {

    public MicroBeanRestException(Throwable cause) {
        super(cause);
    }

    public MicroBeanRestException(String message) {
        super(message);
    }

    public MicroBeanRestException(String message, Throwable cause) {
        super(message, cause);
    }
}
