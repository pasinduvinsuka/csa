package com.smartcampus.exceptions;

/**
 * Thrown when a client attempts to delete a Room that still has
 * active Sensors assigned to it.
 */
public class RoomNotEmptyException extends RuntimeException {
    private final String roomId;

    public RoomNotEmptyException(String roomId) {
        super("Cannot delete room '" + roomId + "' because it still has active sensors assigned to it.");
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
