package com.smartcampus.resources;

import com.smartcampus.models.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Root Discovery Endpoint.
 */
@Path("/")
@Tag(name = "Discovery", description = "API metadata and navigation (HATEOAS)")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "API Discovery",
            description = "Returns essential API metadata including version info, admin contact, "
                    + "and a navigable map of primary resource collections (HATEOAS)."
    )
    public Response getDiscovery() {
        Map<String, Object> discoveryData = new HashMap<>();

        // API Metadata
        discoveryData.put("version", "1.0.0-beta");
        discoveryData.put("admin_contact", "nimsara@smartcampus.edu");
        discoveryData.put("description", "Smart Campus Sensor & Room Management API");

        // Map of primary resource collections (HATEOAS)
        Map<String, String> resources = new HashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");

        discoveryData.put("resources", resources);

        return Response.ok(new ApiResponse(200, "API Discovery", discoveryData)).build();
    }
}