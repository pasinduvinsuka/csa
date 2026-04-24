package com.smartcampus.exceptions;

/**
 * Thrown when a client attempts to POST a reading to a Sensor
 * that is currently in "MAINTENANCE" status.
 */
public class SensorUnavailableException extends RuntimeException {
    private final String sensorId;
    private final String currentStatus;

    public SensorUnavailableException(String sensorId, String currentStatus) {
        super("Sensor '" + sensorId + "' is currently in " + currentStatus
                + " mode and cannot accept new readings.");
        this.sensorId = sensorId;
        this.currentStatus = currentStatus;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }
}
