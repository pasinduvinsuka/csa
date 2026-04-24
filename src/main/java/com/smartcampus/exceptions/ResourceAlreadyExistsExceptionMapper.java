package com.smartcampus.exceptions;

import com.smartcampus.models.ApiResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps ResourceAlreadyExistsException to HTTP 409 Conflict.
 */
@Provider
public class ResourceAlreadyExistsExceptionMapper implements ExceptionMapper<ResourceAlreadyExistsException> {

    @Override
    public Response toResponse(ResourceAlreadyExistsException ex) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ApiResponse(409, ex.getMessage(), null))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
