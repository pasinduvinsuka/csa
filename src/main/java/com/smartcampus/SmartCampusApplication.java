package com.smartcampus;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import java.util.Set;

/**
 * Bootstraps the JAX-RS application with a versioned entry point.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {
    public SmartCampusApplication() {
        // Register packages for resource discovery
        packages("com.smartcampus.resources");

        // Register exception mappers and filters
        packages("com.smartcampus.exceptions");
        packages("com.smartcampus.filters");

        register(org.glassfish.jersey.jackson.JacksonFeature.withoutExceptionMappers());

        // Configure OpenAPI / Swagger
        configureSwagger();
    }

    private void configureSwagger() {
        OpenAPI oas = new OpenAPI()
                .info(new Info()
                        .title("Smart Campus API")
                        .version("1.0.0-beta")
                        .description("RESTful API for managing smart campus")
                        .contact(new Contact()
                                .name("Nimsara Pasindu Vinsuka")
                                .email("nimsara@smartcampus.edu")));

        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true)
                .resourcePackages(Set.of("com.smartcampus.resources"));

        try {
            new JaxrsOpenApiContextBuilder<>()
                    .application(this)
                    .openApiConfiguration(oasConfig)
                    .buildContext(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Swagger/OpenAPI", e);
        }

        // Register the endpoint that serves /openapi.json and /openapi.yaml
        register(OpenApiResource.class);
    }
}