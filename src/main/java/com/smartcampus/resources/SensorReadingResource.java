package com.smartcampus.resources;

import com.smartcampus.data.DataStore;
import com.smartcampus.exceptions.SensorUnavailableException;
import com.smartcampus.models.ApiResponse;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Sensor Readings", description = "Historical reading management for individual sensors")
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore dataStore;

    public SensorReadingResource(String sensorId, DataStore dataStore) {
        this.sensorId = sensorId;
        this.dataStore = dataStore;
    }

    @GET
    @Operation(
            summary = "Get reading history",
            description = "Fetches the full historical log of readings for a specific sensor."
    )
    public Response getHistory() {
        List<SensorReading> history = dataStore.readingsHistory
                .getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(
                new ApiResponse(200, "Reading history for sensor '" + sensorId + "' retrieved successfully.", history)
        ).build();
    }

    @POST
    @Operation(
            summary = "Add a new reading",
            description = "Appends a new reading to the sensor's history and updates the parent sensor's currentValue. "
                    + "Returns 403 if the sensor is in MAINTENANCE mode."
    )
    public Response addReading(SensorReading reading) {
        Sensor parentSensor = dataStore.sensors.get(sensorId);

        if (parentSensor.getStatus() == Sensor.SensorStatus.MAINTENANCE) {
            throw new SensorUnavailableException(sensorId, parentSensor.getStatus().name());
        }

        parentSensor.setCurrentValue(reading.getValue());

        if (reading.getId() == null) reading.setId(UUID.randomUUID().toString());
        if (reading.getTimestamp() == 0) reading.setTimestamp(System.currentTimeMillis());

        dataStore.readingsHistory.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);

        return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse(201, "Reading added successfully. Sensor currentValue updated.", reading))
                .build();
    }
}