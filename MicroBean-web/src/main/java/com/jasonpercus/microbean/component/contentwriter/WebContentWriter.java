package com.jasonpercus.microbean.component.contentwriter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;

public interface WebContentWriter {

    boolean canWrite(Class<?> bodyType);

    List<String> contentTypes();

    String defaultContentType();

    void write(Object body, HttpServletResponse response) throws Exception;
}
