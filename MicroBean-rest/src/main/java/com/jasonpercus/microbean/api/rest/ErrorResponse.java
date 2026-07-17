package com.jasonpercus.microbean.api.rest;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.UUID;

public class ErrorResponse {

    private final UUID correlationId;
    private final int status;
    private final String error;

    public ErrorResponse(
            UUID correlationId,
            int status,
            String error) {

        this.correlationId = correlationId;
        this.status = status;
        this.error = error;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}