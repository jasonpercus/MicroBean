package com.jasonpercus.microbean.infrastructure.exception;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public class HttpServerException extends RuntimeException {

    public HttpServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
