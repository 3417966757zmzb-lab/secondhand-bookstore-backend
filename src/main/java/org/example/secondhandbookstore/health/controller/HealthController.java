package org.example.secondhandbookstore.health.controller;


import lombok.RequiredArgsConstructor;
import org.example.secondhandbookstore.common.result.Result;
import org.example.secondhandbookstore.health.dto.HealthResponse;
import org.example.secondhandbookstore.health.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor //将final的对象注入到构造器中，不用手动注入
@RequestMapping("/api/health")
public class HealthController {
    private final HealthService healthService;

    @GetMapping
    public Result<HealthResponse> getHealth(){

        return Result.success(healthService.getHealth());
    }
}
