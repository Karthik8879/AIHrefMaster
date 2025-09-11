package com.aihref.service;

import com.aihref.model.LookupDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudflareRadarService {
    
    private final WebClient cloudflareClient;
    
    @Value("${external.apis.cloudflare.base-url}")
    private String baseUrl;
    
    @Cacheable(value = "cloudflare", key = "#host")
    public Mono<LookupDocument.LiveData> getLiveData(String url) {
        try {
            String host = new URL(url).getHost();
            log.info("Fetching Cloudflare Radar data for host: {}", host);
            
            return cloudflareClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/traffic/top")
                            .queryParam("name", host)
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(this::mapToLiveData)
                    .doOnSuccess(data -> log.info("Successfully fetched Cloudflare Radar data for {}", host))
                    .doOnError(error -> log.error("Failed to fetch Cloudflare Radar data for {}: {}", host, error.getMessage()))
                    .onErrorReturn(createDefaultLiveData());
                    
        } catch (Exception e) {
            log.error("Error parsing URL {}: {}", url, e.getMessage());
            return Mono.just(createDefaultLiveData());
        }
    }
    
    @SuppressWarnings("unchecked")
    private LookupDocument.LiveData mapToLiveData(Map<String, Object> response) {
        try {
            Map<String, Object> result = (Map<String, Object>) response.get("result");
            if (result == null) {
                return createDefaultLiveData();
            }
            
            java.util.List<Map<String, Object>> top = (java.util.List<Map<String, Object>>) result.get("top");
            if (top == null || top.isEmpty()) {
                return createDefaultLiveData();
            }
            
            Map<String, Object> domainData = top.get(0);
            Integer trafficIndex = extractTrafficIndex(domainData);
            
            return LookupDocument.LiveData.builder()
                    .trafficIndex(trafficIndex != null ? trafficIndex : 0)
                    .lastUpdated(Instant.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("Error mapping Cloudflare Radar response: {}", e.getMessage());
            return createDefaultLiveData();
        }
    }
    
    @SuppressWarnings("unchecked")
    private Integer extractTrafficIndex(Map<String, Object> domainData) {
        try {
            // Cloudflare Radar returns traffic data in different formats
            // This is a simplified extraction - actual implementation would depend on the API response structure
            Object rank = domainData.get("rank");
            if (rank instanceof Number) {
                int rankValue = ((Number) rank).intValue();
                // Convert rank to traffic index (0-100 scale)
                if (rankValue <= 100) return 100 - rankValue;
                if (rankValue <= 1000) return 90 - (rankValue - 100) / 10;
                if (rankValue <= 10000) return 80 - (rankValue - 1000) / 100;
                return Math.max(0, 70 - (rankValue - 10000) / 1000);
            }
            
            // Alternative: look for traffic score or index
            Object trafficScore = domainData.get("traffic_score");
            if (trafficScore instanceof Number) {
                return Math.min(100, Math.max(0, ((Number) trafficScore).intValue()));
            }
            
            return 50; // Default middle value
        } catch (Exception e) {
            log.error("Error extracting traffic index: {}", e.getMessage());
            return 50;
        }
    }
    
    private LookupDocument.LiveData createDefaultLiveData() {
        return LookupDocument.LiveData.builder()
                .trafficIndex(50)
                .lastUpdated(Instant.now())
                .build();
    }
}
