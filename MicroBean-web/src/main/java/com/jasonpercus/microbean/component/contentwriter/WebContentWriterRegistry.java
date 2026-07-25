package com.jasonpercus.microbean.component.contentwriter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public interface WebContentWriterRegistry {

    WebContentWriter find(Object body, String contentType);
}
