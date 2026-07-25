package com.jasonpercus.microbean.infrastructure.model;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public class SwaggerUIConfig {

    private Boolean enabled;
    private SwaggerUIServerConfig server;
    private String path;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public SwaggerUIServerConfig getServer() {
        return server;
    }

    public void setServer(SwaggerUIServerConfig server) {
        this.server = server;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
