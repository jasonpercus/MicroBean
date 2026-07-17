package com.jasonpercus.microbean.api.rest;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.UUID;
import com.jasonpercus.microbean.api.server.context.http.HttpRequestContext;
import com.jasonpercus.microbean.api.server.context.http.HttpResponseContext;
import com.jasonpercus.microbean.api.server.HttpRequestsListener;
import com.jasonpercus.microbean.infrastructure.helpers.LogHelper;

public class DefaultHttpRequestsListener implements HttpRequestsListener {

    @Override
    public void onRequestStart(UUID correlationId, HttpRequestContext request) {
        LogHelper.info("[REQ] [%s] %s %s",
                correlationId.toString(),
                request.getMethod(),
                request.getPath()
        );
    }

    @Override
    public void onRequestEnd(UUID correlationId, HttpRequestContext request, HttpResponseContext response, long durationMillis) {
        LogHelper.info("[RES] [%s] %s %s -> %d (%d ms)",
                correlationId.toString(),
                request.getMethod(),
                request.getPath(),
                response.getStatus(),
                durationMillis
        );
    }
}
