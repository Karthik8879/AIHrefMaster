package com.aihref.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Health check operations")
public class HealthController {
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check the health status of the application")
    @ApiResponse(responseCode = "200", description = "Health check completed")
    public Mono<ResponseEntity<Map<String, Object>>> healthCheck() {
        log.info("Performing health check");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", Instant.now());
        health.put("message", "AIHref API is running");
        health.put("version", "1.0.0");
        
        return Mono.just(ResponseEntity.ok(health))
                .doOnSuccess(response -> log.info("Health check completed successfully"));
    }
}
