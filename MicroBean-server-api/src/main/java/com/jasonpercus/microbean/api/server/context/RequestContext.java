package com.jasonpercus.microbean.api.server.context;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.UUID;

public interface RequestContext {

    UUID correlationId();
}
