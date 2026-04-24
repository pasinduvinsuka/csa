package com.smartcampus.exceptions;

import com.smartcampus.models.ApiResponse;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global "catch-all" safety net for any unexpected runtime errors.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
        // Standard JAX-RS exceptions with original status code
        if (ex instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) ex;
            int status = wae.getResponse().getStatus();
            return Response.status(status)
                    .entity(new ApiResponse(status, wae.getMessage(), null))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Log the full stack trace internally for debugging
        LOGGER.log(Level.SEVERE, "Unhandled exception caught by global safety net", ex);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(500,
                        "An unexpected error occurred on the server. "
                        + "Please contact the administrator if this issue persists.", null))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

