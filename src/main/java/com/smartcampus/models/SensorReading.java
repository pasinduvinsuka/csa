package com.smartcampus.models;

import io.swagger.v3.oas.annotations.media.Schema;

public class SensorReading {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Automatically generated UUID")
    private String id;        // Unique reading event ID
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Automatically generated time")
    private long timestamp;   // time (ms) when reading was captured
    private double value;     // metric value recorded

    // Default constructor required for Jackson JSON mapping
    public SensorReading() {}

    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}