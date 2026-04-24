package com.smartcampus.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.smartcampus.models.ApiResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;

/**
 * Catches ALL Jackson deserialization failures and returns a structured response instead of a raw stack-trace.
 * */
@Provider
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    @Override
    public Response toResponse(JsonProcessingException ex) {
        String message;

        if (ex instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex;
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                message = "Invalid value '" + ife.getValue() + "'. Accepted values are: "
                        + Arrays.toString(ife.getTargetType().getEnumConstants());
            } else {
                message = "Invalid data format provided for one or more fields.";
            }
        } else {
            message = "Unprocessable JSON payload. Ensure the request body is valid JSON "
                    + "matching the expected schema.";
        }

        return Response.status(422)
                .entity(new ApiResponse(422, message, null))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
