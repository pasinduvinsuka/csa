package com.smartcampus.resources;

import com.smartcampus.data.DataStore;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.models.ApiResponse;
import com.smartcampus.models.Sensor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the /api/v1/sensors path.
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore dataStore = DataStore.getInstance();

    @POST
    @Tag(name = "Sensors", description = "Sensor registration, retrieval, and filtering")
    @Operation(
            summary = "Register a new sensor",
            description = "Creates a new sensor linked to an existing room. "
                    + "Returns 422 if the specified roomId does not exist."
    )
    public Response registerSensor(Sensor sensor) {
        if (dataStore.sensors.containsKey(sensor.getId())) {
            throw new com.smartcampus.exceptions.ResourceAlreadyExistsException("Sensor", sensor.getId());
        }

        if (!dataStore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room", sensor.getRoomId());
        }

        dataStore.sensors.put(sensor.getId(), sensor);
        dataStore.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse(201, "Sensor registered successfully.", sensor))
                .build();
    }

    @GET
    @Tag(name = "Sensors", description = "Sensor registration, retrieval, and filtering")
    @Operation(
            summary = "List all sensors",
            description = "Returns all sensors, optionally filtered by type (e.g.,type=CO2)."
    )
    public Response getSensors(
            @Parameter(description = "Filter by sensor type (e.g., Temperature, CO2)", example = "CO2")
            @QueryParam("type") String type) {
        List<Sensor> result;
        String message;

        if (type != null && !type.isEmpty()) {
            result = dataStore.sensors.values().stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
            message = "Sensors filtered by type '" + type + "' retrieved successfully.";
        } else {
            result = new ArrayList<>(dataStore.sensors.values());
            message = "All sensors retrieved successfully.";
        }

        return Response.ok(new ApiResponse(200, message, result)).build();
    }

    @Path("{sensorId}/readings")
    public SensorReadingResource getReadingResource(
            @PathParam("sensorId") String sensorId) {
        if (!dataStore.sensors.containsKey(sensorId)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(new ApiResponse(404, "Sensor with ID '" + sensorId + "' was not found.", null))
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        return new SensorReadingResource(sensorId, dataStore);
    }
}