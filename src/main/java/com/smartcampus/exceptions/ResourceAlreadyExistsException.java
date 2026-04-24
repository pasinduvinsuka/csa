package com.smartcampus.exceptions;

/**
 * Thrown when attempting to create a resource (Room or Sensor) 
 * with an ID that is already registered in the system.
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String resourceType, String id) {
        super(resourceType + " with ID '" + id + "' already exists.");
    }
}
