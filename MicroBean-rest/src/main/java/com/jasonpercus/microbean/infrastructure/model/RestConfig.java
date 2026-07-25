package com.jasonpercus.microbean.infrastructure.model;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.infrastructure.api.ModeLog;

public class RestConfig {

    private Boolean enabled;
    private RestServerConfig server;
    private String path;
    private ModeLog modeLog;
    private String httpRequestsListenerClassname;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public RestServerConfig getServer() {
        return server;
    }

    public void setServer(RestServerConfig server) {
        this.server = server;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ModeLog getModeLog() {
        return modeLog;
    }

    public void setModeLog(ModeLog modeLog) {
        this.modeLog = modeLog;
    }

    public String getHttpRequestsListenerClassname() {
        return httpRequestsListenerClassname;
    }

    public void setHttpRequestsListenerClassname(String httpRequestsListenerClassname) {
        this.httpRequestsListenerClassname = httpRequestsListenerClassname;
    }
}
