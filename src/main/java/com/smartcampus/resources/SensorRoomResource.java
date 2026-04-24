package com.smartcampus.resources;

import com.smartcampus.data.DataStore;
import com.smartcampus.exceptions.RoomNotEmptyException;
import com.smartcampus.models.ApiResponse;
import com.smartcampus.models.Room;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Manages the /api/v1/rooms path.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Rooms", description = "Room management operations (CRUD)")
public class SensorRoomResource {

    private final DataStore dataStore = DataStore.getInstance();

    @GET
    @Operation(summary = "List all rooms", description = "Returns a comprehensive list of all rooms on campus.")
    public Response getAllRooms() {
        return Response.ok(
                new ApiResponse(200, "Rooms retrieved successfully.",
                        new ArrayList<>(dataStore.rooms.values()))
        ).build();
    }

    @POST
    @Operation(summary = "Create a new room", description = "Registers a new room in the system. Returns 201 Created on success.")
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(400, "Room ID is required.", null))
                    .build();
        }
        
        if (dataStore.rooms.containsKey(room.getId())) {
            throw new com.smartcampus.exceptions.ResourceAlreadyExistsException("Room", room.getId());
        }

        dataStore.rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED)
                .entity(new ApiResponse(201, "Room created successfully.", room))
                .build();
    }

    @GET
    @Path("/{roomId}")
    @Operation(summary = "Get room by ID", description = "Fetches detailed metadata for a specific room.")
    public Response getRoomById(
            @Parameter(description = "Unique room identifier", example = "LIB-301")
            @PathParam("roomId") String roomId) {
        Room room = dataStore.rooms.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(404, "Room with ID '" + roomId + "' was not found.", null))
                    .build();
        }
        return Response.ok(new ApiResponse(200, "Room retrieved successfully.", room)).build();
    }

    @DELETE
    @Path("/{roomId}")
    @Operation(
            summary = "Delete a room",
            description = "Decommissions a room."
    )
    public Response deleteRoom(
            @Parameter(description = "Unique room identifier", example = "LIB-301")
            @PathParam("roomId") String roomId) {
        Room room = dataStore.rooms.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(404, "Room with ID '" + roomId + "' was not found.", null))
                    .build();
        }

        boolean hasLinkedSensors = dataStore.sensors.values().stream()
                .anyMatch(sensor -> sensor.getRoomId().equals(roomId));

        if (hasLinkedSensors) {
            throw new RoomNotEmptyException(roomId);
        }

        dataStore.rooms.remove(roomId);

        return Response.ok(
                new ApiResponse(200, "Room '" + roomId + "' deleted successfully.", null)
        ).build();
    }
}