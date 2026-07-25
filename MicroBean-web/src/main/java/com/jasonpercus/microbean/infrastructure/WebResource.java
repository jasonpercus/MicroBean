package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import java.io.InputStream;

public interface WebResource {

    InputStream inputStream() throws IOException;
    String contentType() throws IOException;
    long contentLength() throws IOException;
}
