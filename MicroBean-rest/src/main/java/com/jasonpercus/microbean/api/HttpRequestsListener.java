package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.UUID;
import com.jasonpercus.microbean.infrastructure.HttpRequestContext;
import com.jasonpercus.microbean.infrastructure.HttpResponseContext;

public interface HttpRequestsListener {

    default void onRequestStart(UUID correlationId, HttpRequestContext request) {

    }

    default void onRequestEnd(UUID correlationId, HttpRequestContext request, HttpResponseContext response, long durationMillis) {

    }
}
