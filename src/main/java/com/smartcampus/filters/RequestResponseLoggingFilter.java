package com.smartcampus.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * API observability filter.
 * Implements both ContainerRequestFilter and ContainerResponseFilter
 * to log every incoming request (method + URI) and outgoing response (status code).
 */
@Provider
public class RequestResponseLoggingFilter
        implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(RequestResponseLoggingFilter.class.getName());

    /**
     * Logs the HTTP method and request URI for every incoming request.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.info("REQUEST  | " + requestContext.getMethod()
                + " " + requestContext.getUriInfo().getRequestUri());
    }

    /**
     * Logs the HTTP status code for every outgoing response.
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LOGGER.info("RESPONSE | " + requestContext.getMethod()
                + " " + requestContext.getUriInfo().getRequestUri()
                + " - " + responseContext.getStatus()
                + " " + responseContext.getStatusInfo().getReasonPhrase());
    }
}
