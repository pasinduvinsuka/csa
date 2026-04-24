package com.smartcampus;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import java.net.URI;

public class Main {
    // base URI where the server will listen
    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static void main(String[] args) {
        // Start the server using the configuration in SmartCampusApplication
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                URI.create(BASE_URI), new SmartCampusApplication());

        // Serve Swagger UI
        CLStaticHttpHandler swaggerHandler = new CLStaticHttpHandler(
                Main.class.getClassLoader(),
                "swagger-ui/",
                "META-INF/resources/webjars/swagger-ui/4.15.5/"
        );
        server.getServerConfiguration().addHttpHandler(swaggerHandler, "/swagger-ui/");

        System.out.println("Smart Campus API is live!");
        System.out.println("Base URL: " + BASE_URI);
        System.out.println("Try hitting the Discovery endpoint: " + BASE_URI);
        System.out.println("");
        System.out.println("OpenAPI Spec:  " + BASE_URI + "openapi.json");
        System.out.println("Swagger UI:    http://localhost:8080/swagger-ui/index.html");
    }
}