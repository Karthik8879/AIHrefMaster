package com.aihref.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class RateLimitConfig {
    
    @Value("${rate-limit.requests-per-minute:120}")
    private int requestsPerMinute;
    
    @Value("${rate-limit.window-size-minutes:1}")
    private int windowSizeMinutes;
    
    @Bean
    public WebFilter noOpRateLimitFilter() {
        return (exchange, chain) -> chain.filter(exchange);
    }
}