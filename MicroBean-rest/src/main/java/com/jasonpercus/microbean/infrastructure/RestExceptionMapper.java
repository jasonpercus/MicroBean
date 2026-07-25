package com.jasonpercus.microbean.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.UUID;
import com.jasonpercus.microbean.infrastructure.helpers.LogHelper;
import com.jasonpercus.microbean.infrastructure.model.ErrorResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable error) {

        UUID correlationId = RequestContextHolder.current().correlationId();

        LogHelper.error("[ERR] [%s] %s".formatted(correlationId.toString(), error.getMessage()), error);

        ErrorResponse errorResponse;

        if (error instanceof WebApplicationException e) {
            errorResponse = new ErrorResponse(
                    correlationId,
                    e.getResponse().getStatusInfo().toEnum().getStatusCode(),
                    error.getMessage()
            );
        } else {
            errorResponse = new ErrorResponse(
                    correlationId,
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    "Internal Server Error"
            );
        }

        return Response.status(errorResponse.getStatus())
                .entity(errorResponse)
                .build();
    }
}
