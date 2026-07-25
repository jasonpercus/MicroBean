package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public final class RequestContextHolder {

    private static final ThreadLocal<RequestContext> CURRENT = new ThreadLocal<>();

    public static RequestContext current() {
        return CURRENT.get();
    }

    public static void set(RequestContext context) {
        CURRENT.set(context);
    }

    public static void clear() {
        CURRENT.remove();
    }
}
