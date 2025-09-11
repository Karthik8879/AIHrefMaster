package com.aihref.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class RateLimitWebFilter implements WebFilter {
    
    private final ReactiveStringRedisTemplate redisTemplate;
    private final int requestsPerMinute;
    private final int windowSizeMinutes;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String clientIp = getClientIp(exchange);
        String key = "rate_limit:" + clientIp;
        
        return redisTemplate.opsForValue()
                .increment(key)
                .flatMap(count -> {
                    if (count == 1) {
                        // Set expiration on first request
                        return redisTemplate.expire(key, Duration.ofMinutes(windowSizeMinutes))
                                .then(Mono.just(count));
                    }
                    return Mono.just(count);
                })
                .flatMap(count -> {
                    if (count > requestsPerMinute) {
                        log.warn("Rate limit exceeded for IP: {} ({} requests)", clientIp, count);
                        return handleRateLimitExceeded(exchange);
                    }
                    return chain.filter(exchange);
                })
                .onErrorResume(error -> {
                    log.error("Error in rate limiting for IP {}: {}", clientIp, error.getMessage());
                    // Continue with request if Redis is down
                    return chain.filter(exchange);
                });
    }
    
    private String getClientIp(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return exchange.getRequest().getRemoteAddress() != null 
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
    }
    
    private Mono<Void> handleRateLimitExceeded(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Retry-After", String.valueOf(windowSizeMinutes * 60));
        response.getHeaders().add("Content-Type", "application/json");
        
        String errorBody = String.format(
                "{\"error\":\"Rate limit exceeded\",\"message\":\"Too many requests. Limit: %d per %d minutes\",\"retryAfter\":%d}",
                requestsPerMinute, windowSizeMinutes, windowSizeMinutes * 60
        );
        
        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorBody.getBytes())));
    }
}
