package com.aihref.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
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
@RequiredArgsConstructor
@Tag(name = "Health", description = "Health check operations")
public class HealthController {
    
    private final ReactiveMongoTemplate mongoTemplate;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check the health status of the application and its dependencies")
    @ApiResponse(responseCode = "200", description = "Health check completed")
    public Mono<ResponseEntity<Map<String, Object>>> healthCheck() {
        log.info("Performing health check");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", Instant.now());
        
        return checkMongoHealth()
                .flatMap(mongoHealth -> {
                    health.put("mongo", mongoHealth);
                    return checkRedisHealth();
                })
                .map(redisHealth -> {
                    health.put("redis", redisHealth);
                    return ResponseEntity.ok(health);
                })
                .doOnSuccess(response -> log.info("Health check completed successfully"))
                .doOnError(error -> log.error("Health check failed: {}", error.getMessage()));
    }
    
    private Mono<Map<String, Object>> checkMongoHealth() {
        return mongoTemplate.getCollection("lookups")
                .countDocuments()
                .map(count -> {
                    Map<String, Object> mongoHealth = new HashMap<>();
                    mongoHealth.put("status", "UP");
                    mongoHealth.put("documentCount", count);
                    return mongoHealth;
                })
                .onErrorReturn(createErrorHealth("MongoDB", "DOWN"));
    }
    
    private Mono<Map<String, Object>> checkRedisHealth() {
        return redisTemplate.opsForValue()
                .get("health:check")
                .then(redisTemplate.opsForValue().set("health:check", "ok"))
                .map(result -> {
                    Map<String, Object> redisHealth = new HashMap<>();
                    redisHealth.put("status", "UP");
                    redisHealth.put("connection", "OK");
                    return redisHealth;
                })
                .onErrorReturn(createErrorHealth("Redis", "DOWN"));
    }
    
    private Map<String, Object> createErrorHealth(String service, String status) {
        Map<String, Object> health = new HashMap<>();
        health.put("status", status);
        health.put("error", service + " connection failed");
        return health;
    }
}
