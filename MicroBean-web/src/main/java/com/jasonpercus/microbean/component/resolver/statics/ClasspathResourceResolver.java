package com.jasonpercus.microbean.component.resolver.statics;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import com.jasonpercus.microbean.infrastructure.WebResource;

public class ClasspathResourceResolver implements ResourceResolver {

    private final String root;

    public ClasspathResourceResolver(String root) {
        this.root = root;
    }

    @Override
    public WebResource resolve(String path) {

        if (path.equals("/"))
            path = "/index.html";

        final String resourcePath = root + path;

        URL url = ClasspathResourceResolver.class.getClassLoader().getResource("."+ resourcePath);

        if (url == null)
            return null;

        try {

            URLConnection connection = url.openConnection();

            long length = connection.getContentLengthLong();
            String contentType = contentType(path);

            InputStream input = connection.getInputStream();

            if (input == null)
                return null;

            return new WebResource() {

                @Override
                public InputStream inputStream() throws IOException {
                    return url.openStream();
                }

                @Override
                public String contentType() {
                    return contentType;
                }

                @Override
                public long contentLength() {
                    return length;
                }
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String contentType(String path) {

        if (path.endsWith(".html"))
            return "text/html";

        if (path.endsWith(".css"))
            return "text/css";

        if (path.endsWith(".js"))
            return "application/javascript";

        if (path.endsWith(".png"))
            return "image/png";

        if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
            return "image/jpeg";

        return "application/octet-stream";
    }
}
