package com.smartcampus.models;

public class Sensor {
    public enum SensorStatus {
        ACTIVE, MAINTENANCE, OFFLINE
    }

    private String id;          // Unique identifier
    private String type;        // Category
    private SensorStatus status; // enum for the 3 allowed states
    private double currentValue; // Most recent measurement recorded
    private String roomId;      // Foreign key linking to the Room

    // Default constructor required for Jackson JSON mapping
    public Sensor() {}

    public Sensor(String id, String type, SensorStatus status, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.roomId = roomId;
        this.currentValue = 0.0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public SensorStatus getStatus() { return status; }
    public void setStatus(SensorStatus status) { this.status = status; }

    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}