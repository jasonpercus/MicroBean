package com.jasonpercus.microbean.infrastructure.model;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import io.swagger.v3.oas.models.OpenAPI;

public record OpenApiDefinition(String name, String path, OpenAPI document) {

}
