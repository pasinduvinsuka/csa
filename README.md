# Smart Campus Sensor & Room Management API

This is a REST API built with JAX-RS (Jersey) and Grizzly for managing smart campus rooms, sensors, and sensor readings. It uses in-memory data structures (ConcurrentHashMap) to store data safely, custom exception mappers to handle errors, and a JAX-RS filter to log requests.

## API Design Overview

The API is structured around the main resources:

- **/api/v1**                          - Discovery endpoint
- **/api/v1/rooms**                    - Manage rooms (CRUD)
- **/api/v1/rooms/{roomId}**           - Manage a single room
- **/api/v1/sensors**                  - Manage sensors (CRUD and filtering)
- **/api/v1/sensors/{sensorId}/readings** - Manage readings for a specific sensor

### Design Choices

- **API Versioning**: All endpoints start with `/api/v1`.
- **Discovery Endpoint**: The root endpoint provides links to all resource collections so clients don't have to guess the URLs.
- **Nested Resources**: Sensor readings are handled by a separate `SensorReadingResource` class to keep the code organized.
- **Data Storage**: We use a `DataStore` singleton class with `ConcurrentHashMap` to keep data safe when multiple requests come in at the same time.
- **Error Handling**: Custom exceptions like `RoomNotEmptyException` are mapped to HTTP status codes (like 409 or 422) using `@Provider` mappers.
- **Logging**: A single filter logs all incoming requests and outgoing responses.

### Tech Stack

- Framework: JAX-RS 2.1 (Jersey 2.41)
- Server: Grizzly 2
- JSON: Jackson
- Build Tool: Maven
- Java Version: JDK 21

---

## Project Structure

```
smart-campus-api/
├── pom.xml                          # Maven configuration
├── README.md                        # Readme
└── src/main/java/com/smartcampus/
    ├── Main.java                    # Entry point 
    ├── SmartCampusApplication.java  # JAX-RS Application config (@ApplicationPath)
    ├── data/
    │   └── DataStore.java           
    ├── models/
    │   ├── Room.java                # Room POJO
    │   ├── Sensor.java              # Sensor POJO
    │   └── SensorReading.java       # SensorReading POJO
    ├── resources/
    │   ├── DiscoveryResource.java   # GET /api/v1 — API metadata & HATEOAS
    │   ├── SensorRoomResource.java  # /api/v1/rooms — Room CRUD
    │   ├── SensorResource.java      # /api/v1/sensors — Sensor CRUD + sub-resource locator
    │   └── SensorReadingResource.java # Sub-resource for sensor readings history
    ├── exceptions/
    │   ├── RoomNotEmptyException.java                # Custom: 409 Conflict
    │   ├── RoomNotEmptyExceptionMapper.java          # Maps to 409 JSON
    │   ├── LinkedResourceNotFoundException.java      # Custom: 422 Unprocessable Entity
    │   ├── LinkedResourceNotFoundExceptionMapper.java # Maps to 422 JSON
    │   ├── SensorUnavailableException.java           # Custom: 403 Forbidden
    │   ├── SensorUnavailableExceptionMapper.java     # Maps to 403 JSON
    │   └── GenericExceptionMapper.java               # Catch-all: 500 Internal Server Error
    └── filters/
        └── RequestResponseLoggingFilter.java         # Request/Response logging filter
```


## How to Build and Run

### Prerequisites
- JDK 21
- Maven

### Step 1: Clone the Repository
```bash
git clone https://github.com/pasinduvinsuka/csa.git
cd csa
```

### Step 2: Build the Project
```bash
mvn clean compile
```

### Step 3: Launch the Server

**Using Maven:**
```bash
mvn exec:java
```

### Step 4: Verify
Once it is running, you can test it by going to `http://localhost:8080/api/v1/` in your browser.

---

## Sample curl Commands

### 1. Get API Metadata
```bash
curl -X GET http://localhost:8080/api/v1/
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "API Discovery",
  "data": {
    "admin_contact": "nimsara@smartcampus.edu",
    "description": "Smart Campus Sensor & Room Management API",
    "resources": {
      "rooms": "/api/v1/rooms",
      "sensors": "/api/v1/sensors"
    },
    "version": "1.0.0-beta"
  }
}
```

### 2. Create a Room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "LIB-301", "name": "Library Quiet Study", "capacity": 50}'
```

**Expected Response:**
```json
{
  "status": 201,
  "message": "Room created successfully.",
  "data": {
    "id": "LIB-301",
    "name": "Library Quiet Study",
    "capacity": 50,
    "sensorIds": []
  }
}
```

### 3. Register a Sensor
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-001", "type": "Temperature", "status": "ACTIVE", "roomId": "LIB-301"}'
```

**Expected Response:**
```json
{
  "status": 201,
  "message": "Sensor registered successfully.",
  "data": {
    "id": "TEMP-001",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 0.0,
    "roomId": "LIB-301"
  }
}
```

### 4. Post a Sensor Reading
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 23.5}'
```

**Expected Response:**
```json
{
  "status": 201,
  "message": "Reading added successfully. Sensor currentValue updated.",
  "data": {
    "id": "0b31dc2c-6b9a-4e53-bb5f-3d21977b2afa",
    "timestamp": 1713898685123,
    "value": 23.5
  }
}
```

### 5. Filter Sensors by Type
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"
```

**Expected Response:**
```json
{
  "status": 200,
  "message": "Sensors filtered by type 'Temperature' retrieved successfully.",
  "data": [
    {
      "id": "TEMP-001",
      "type": "Temperature",
      "status": "ACTIVE",
      "currentValue": 23.5,
      "roomId": "LIB-301"
    }
  ]
}
```

### 6. Delete a Room with Sensors (Error Case)
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

**Expected Response:**
```json
{
  "status": 409,
  "message": "Cannot delete room 'LIB-301' because it still has active sensors assigned to it.",
  "data": null
}
```

### 7. Register a Sensor with Invalid Room ID (Error Case)
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "CO2-999", "type": "CO2", "status": "ACTIVE", "roomId": "NONEXISTENT"}'
```

**Expected Response:**
```json
{
  "status": 422,
  "message": "Linked Room with ID 'NONEXISTENT' does not exist in the system.",
  "data": null
}
```

---

## Coursework Report Q&A

### Part 1: Service Architecture & Setup

**Q1: Default Lifecycle of a JAX-RS Resource Class :**
Default Lifecycle of a JAX-RS Resource Class JAX-RS uses the resource class to instantiate a new instance to each incoming HTTP request. After the request has been made, the instance is discarded. Due to this, we are not able to store our data in standard class variables as they would be cleared whenever a new request is received. Rather, we store the data in a singleton `DataStore`. We also need to utilize `ConcurrentHashMap` in such a way that when more than one request is making attempts to modify the data simultaneously, then it does not cause problems.

**Q2: Why is HATEOAS Important? :**
HATEOAS helps clients navigate the API without needing to hard-code URLs. By including links in the response, clients can just follow the links provided by the server. If the server changes a URL later, the client won't break because it just follows whatever link the server gives it.

### Part 2: Room Management

**Q1: Returning Full Objects vs. Only IDs :**
Full objects implies that the client obtains all the data that it requires in a single call, and this simplifies processing on the client side because no additional retrieval code will be required. The disadvantage is that it uses more network bandwidth since the JSON response is bigger. Saving network bandwidth by only sending IDs initially, but making the client-side processing more complex since it needs to make a large number of additional requests to receive the information about each room.

**Q2: Is DELETE Idempotent? :**
Yes. Idempotent implies that it can be done only once or several times and the result is the same state of the server. In case the client removes a room, it is lost. Client will also receive a 404 error in case the client once again tries to delete it, however, the final result on the server remains the same.

### Part 3: Sensor Operations & Linking

**Q1: Consequences of Sending Non-JSON Data with @Consumes(APPLICATION_JSON) :**
When a client post data other than `application/json`, such as plain text or XML, JAX-RS will notice it does not match `application/json` and will automatically block it. Before the code in the method even executes, it results in a 415 Unsupported Media Type error.


**Q2: @QueryParam vs. Path-Based Filtering :**
Using query parameters (like `?type=CO2`) is better for filtering because:
- They are optional. The same endpoint works with or without them.
- Client can easily combine multiple filters.
- In REST, URL paths should identify resources, while query parameters are meant for filtering those resources.

### Part 4: Deep Nesting with Sub-Resources

**Q1: Benefits of the Sub-Resource Locator Pattern :**
By delegating `/sensors/{sensorId}/readings` to a `SensorReadingResource` class, we keep the code clean. `SensorResource` only handles sensor stuff, and `SensorReadingResource` handles the readings. This stops the controller classes from getting too big and complicated, and makes them easier to test.

### Part 5: Advanced Error Handling, Exception Mapping & Logging

**Q1 (section 2): Why is HTTP 422 More Semantically Accurate Than 404? :**
In an attempt to make a sensor in a room that does not exist, the URL `/api/v1/sensors` is indeed right. The use of a 404 would mean that the URL is incorrect. The error 422 indicates that the server received the request and it was in the correct format of a JSON, however, the information contained within the JSON (the missing room ID) was the cause of the issue.

**Q2 (section 4): Cybersecurity Risks of Exposing Stack Traces :**
If we show stack traces to users, attackers can see exactly what versions of Java and libraries we are using. They could also see file paths and get clues about how our internal code works, which helps them find vulnerabilities. Our `GenericExceptionMapper` stops this by logging the error internally and just giving the user a generic error message.

**Q3 (section 5): Why Use JAX-RS Filters for Logging? :**
Filtering involves having to write the logging code only once, and then it is automatically applied to all requests and responses. Without the filter we would have to copy and paste `Logger.info()` into each individual method, which is not very tidy and forgettable.

