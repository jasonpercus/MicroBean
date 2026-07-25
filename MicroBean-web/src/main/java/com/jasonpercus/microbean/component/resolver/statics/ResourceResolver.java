package com.jasonpercus.microbean.component.resolver.statics;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import com.jasonpercus.microbean.infrastructure.WebResource;

public interface ResourceResolver {

    WebResource resolve(String path) throws IOException;

    static ResourceResolver create(String root) {

        if (root.startsWith("classpath:"))
            return new ClasspathResourceResolver(root.substring("classpath:".length()));

        if (root.startsWith("file:"))
            return new FileSystemResourceResolver(root.substring("file:".length()));

        throw new IllegalArgumentException("Unsupported resource root: " + root);
    }
}
