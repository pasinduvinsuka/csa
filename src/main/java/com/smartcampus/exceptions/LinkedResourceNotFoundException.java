package com.smartcampus.exceptions;

/**
 * Thrown when a client attempts to create a resource (e.g., Sensor)
 * that references a non-existent linked resource (e.g., roomId)
 * inside its JSON payload.
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    private final String resourceType;
    private final String resourceId;

    public LinkedResourceNotFoundException(String resourceType, String resourceId) {
        super("Linked " + resourceType + " with ID '" + resourceId + "' does not exist in the system.");
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}
