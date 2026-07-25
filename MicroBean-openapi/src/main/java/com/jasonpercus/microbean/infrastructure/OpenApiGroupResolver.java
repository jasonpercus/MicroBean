package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.jasonpercus.microbean.api.OpenApiGroup;

public class OpenApiGroupResolver {

    public Map<String, List<Object>> resolve(List<Object> controllers) {

        Map<String, List<Object>> groups = new LinkedHashMap<>();

        for (Object controller : controllers) {

            OpenApiGroup annotation = controller.getClass().getAnnotation(OpenApiGroup.class);

            String key = annotation != null ? "/%s.json".formatted(annotation.value()) : "/openapi.json";

            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(controller);
        }

        return groups;
    }
}
