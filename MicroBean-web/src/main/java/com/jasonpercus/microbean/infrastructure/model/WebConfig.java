package com.jasonpercus.microbean.infrastructure.model;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

public class WebConfig {

    private Boolean enabled;
    private WebServerConfig server;
    private String path;
    private String root;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public WebServerConfig getServer() {
        return server;
    }

    public void setServer(WebServerConfig server) {
        this.server = server;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}
