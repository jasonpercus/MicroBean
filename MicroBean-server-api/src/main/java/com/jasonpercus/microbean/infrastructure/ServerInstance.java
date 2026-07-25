package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.jasonpercus.microbean.infrastructure.api.IModuleHttpServer;

public class ServerInstance {

    String provider;
    String host;
    int port;
    List<IModuleHttpServer> modules;

    public ServerInstance(String provider, String host, int port) {
        this.provider = provider;
        this.host = host;
        this.port = port;
        this.modules = new ArrayList<>();
    }

    public String getProvider() {
        return provider;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public List<IModuleHttpServer> getModules() {
        return modules;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        ServerInstance that = (ServerInstance) object;
        return port == that.port && Objects.equals(provider, that.provider) && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, host, port);
    }
}
