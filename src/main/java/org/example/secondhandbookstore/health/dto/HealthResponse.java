package org.example.secondhandbookstore.health.dto;



public record HealthResponse(
        String status,
        String application,
        String version
){}
