package com.smartcampus.models;

/**
 * Standard API response wrapper for consistent response structure.
 * Every endpoint returns with the following format:
 * {
 *   "status": 200,
 *   "message": "Success message",
 *   "data": { ... } or [ ... ] or null
 * }
 */
public class ApiResponse {
    private int status;
    private String message;
    private Object data;

    public ApiResponse() {}

    public ApiResponse(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
