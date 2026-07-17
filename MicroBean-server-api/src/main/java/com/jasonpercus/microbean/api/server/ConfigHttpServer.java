package com.jasonpercus.microbean.api.server;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.Objects;

public class ConfigHttpServer {

    private final String hostname;
    private final int port;
    private final boolean logRequests;

    public ConfigHttpServer() {
        this.hostname = "localhost";
        this.port = 8080;
        this.logRequests = true;
    }

    public ConfigHttpServer(String hostname) {
        this.hostname = hostname;
        this.port = 8080;
        this.logRequests = true;
    }

    public ConfigHttpServer(int port) {
        this.hostname = "localhost";
        this.port = port;
        this.logRequests = true;
    }

    public ConfigHttpServer(boolean logRequests) {
        this.hostname = "localhost";
        this.port = 8080;
        this.logRequests = logRequests;
    }

    public ConfigHttpServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.logRequests = true;
    }

    public ConfigHttpServer(int port, boolean logRequests) {
        this.hostname = "localhost";
        this.port = port;
        this.logRequests = logRequests;
    }

    public ConfigHttpServer(String hostname, boolean logRequests) {
        this.hostname = hostname;
        this.port = 8080;
        this.logRequests = logRequests;
    }

    public ConfigHttpServer(String hostname, int port, boolean logRequests) {
        this.hostname = hostname;
        this.port = port;
        this.logRequests = logRequests;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }
    
    public boolean islogRequests() {
        return logRequests;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        ConfigHttpServer that = (ConfigHttpServer) object;
        return getPort() == that.getPort() && Objects.equals(getHostname(), that.getHostname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHostname(), getPort());
    }

    @Override
    public String toString() {
        return "ConfigHttpServer{" +
                "hostname='" + hostname + '\'' +
                ", port=" + port +
                '}';
    }
}
