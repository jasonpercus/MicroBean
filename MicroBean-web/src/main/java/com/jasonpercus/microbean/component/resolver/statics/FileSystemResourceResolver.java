package com.jasonpercus.microbean.component.resolver.statics;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.jasonpercus.microbean.infrastructure.WebResource;

public class FileSystemResourceResolver implements ResourceResolver {

    private final Path root;

    public FileSystemResourceResolver(String root) {
        this.root = Paths.get(root).toAbsolutePath().normalize();
    }

    @Override
    public WebResource resolve(String path) throws IOException {

        if (path.equals("/"))
            path = "/index.html";

        Path file = root.resolve(path.substring(1)).normalize();

        if (!file.startsWith(root))
            return null;

        if (!Files.exists(file) || Files.isDirectory(file))
            return null;

        return new WebResource() {

            @Override
            public InputStream inputStream() throws IOException {
                return Files.newInputStream(file);
            }

            @Override
            public String contentType() throws IOException {
                return Files.probeContentType(file);
            }

            @Override
            public long contentLength() throws IOException {
                return Files.size(file);
            }
        };
    }
}
