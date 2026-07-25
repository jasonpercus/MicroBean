package com.jasonpercus.microbean.component.contentwriter;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.List;
import java.util.Objects;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.WebExtension;

@WebExtension(name = "MicroBean")
public class DefaultWebContentWriterRegistry implements WebContentWriterRegistry {

    private final List<WebContentWriter> writers;

    public DefaultWebContentWriterRegistry() {
        this.writers = MicroBean.getContext().getBeanTypesByAnnotation(WebExtension.class)
                .stream()
                .filter(WebContentWriter.class::isAssignableFrom)
                .map(c -> (WebContentWriter) MicroBean.getContext().getBean(c))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public WebContentWriter find(Object body, String contentType) {

        Class<?> type = body.getClass();

        if (contentType != null) {
            return writers.stream()
                    .filter(w -> w.canWrite(type))
                    .filter(w ->
                            w.contentTypes().isEmpty() || w.contentTypes().stream().anyMatch(contentType::startsWith))
                    .findFirst()
                    .orElseThrow(() -> getIllegalStateException(contentType));
        }

        return writers.stream()
                .filter(w -> w.canWrite(type))
                .findFirst()
                .orElseThrow(() -> getIllegalStateException(null));
    }

    private static IllegalStateException getIllegalStateException(String contentType) {
        return new IllegalStateException("No WebContentWriter found for Content-Type " + contentType);
    }
}
