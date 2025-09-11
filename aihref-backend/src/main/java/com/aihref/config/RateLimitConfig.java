package com.aihref.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.web.server.WebFilter;

import java.time.Duration;

@Configuration
public class RateLimitConfig {
    
    @Value("${rate-limit.requests-per-minute:120}")
    private int requestsPerMinute;
    
    @Value("${rate-limit.window-size-minutes:1}")
    private int windowSizeMinutes;
    
    @Bean
    public WebFilter rateLimitFilter(ReactiveStringRedisTemplate redisTemplate) {
        return new RateLimitWebFilter(redisTemplate, requestsPerMinute, windowSizeMinutes);
    }
}
