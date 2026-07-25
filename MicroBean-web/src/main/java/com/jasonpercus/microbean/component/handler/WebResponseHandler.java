package com.jasonpercus.microbean.component.handler;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.infrastructure.WebResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface WebResponseHandler {

    void write(WebResponse response, HttpServletResponse servletResponse) throws Exception;
}
