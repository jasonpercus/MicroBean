package com.jasonpercus.microbean.api.server.context.http;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public interface HttpResponseContext {

    ServletRequest getRequest();

    ServletResponse getResponse();

    int getStatus();
}
