package com.jasonpercus.microbean.openapi;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OpenApiRegistry {

    private final List<OpenApiDefinition> definitions = new CopyOnWriteArrayList<>();

    public void register(OpenApiDefinition definition) {
        definitions.add(definition);
    }

    public List<OpenApiDefinition> getDefinitions() {
        return List.copyOf(definitions);
    }
}
