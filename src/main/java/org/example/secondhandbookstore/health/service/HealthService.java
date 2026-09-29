package org.example.secondhandbookstore.health.service;



import org.example.secondhandbookstore.health.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private final String applicationName;
    private final String version;

    public HealthService(
            @Value("${app.name}") String applicationName,
            @Value("${app.version}") String version
    ){
        this.applicationName = applicationName;
        this.version = version;
    }
     public HealthResponse getHealth(){
        return new HealthResponse("UP",applicationName,version);
     }
}
