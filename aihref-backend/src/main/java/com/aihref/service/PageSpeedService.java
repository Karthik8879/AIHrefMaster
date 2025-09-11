package com.aihref.service;

import com.aihref.model.LookupDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageSpeedService {
    
    private final WebClient pageSpeedClient;
    
    @Value("${external.apis.pagespeed.base-url}")
    private String baseUrl;
    
    @Value("${external.apis.pagespeed.api-key:}")
    private String apiKey;
    
    @Cacheable(value = "pagespeed", key = "#url")
    public Mono<LookupDocument.WebVitalsData> getWebVitalsData(String url) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("PageSpeed API key not configured, returning default data");
            return Mono.just(createDefaultWebVitalsData());
        }
        
        log.info("Fetching PageSpeed data for URL: {}", url);
        
        return pageSpeedClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/runPagespeed")
                        .queryParam("url", url)
                        .queryParam("key", apiKey)
                        .queryParam("strategy", "mobile")
                        .queryParam("category", "performance")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::mapToWebVitalsData)
                .doOnSuccess(data -> log.info("Successfully fetched PageSpeed data for {}", url))
                .doOnError(error -> log.error("Failed to fetch PageSpeed data for {}: {}", url, error.getMessage()))
                .onErrorReturn(createDefaultWebVitalsData());
    }
    
    @SuppressWarnings("unchecked")
    private LookupDocument.WebVitalsData mapToWebVitalsData(Map<String, Object> response) {
        try {
            Map<String, Object> lighthouseResult = (Map<String, Object>) response.get("lighthouseResult");
            if (lighthouseResult == null) {
                return createDefaultWebVitalsData();
            }
            
            Map<String, Object> audits = (Map<String, Object>) lighthouseResult.get("audits");
            Map<String, Object> categories = (Map<String, Object>) lighthouseResult.get("categories");
            
            // Extract Core Web Vitals
            Double lcp = extractMetricValue(audits, "largest-contentful-paint", "numericValue");
            Double fid = extractMetricValue(audits, "max-potential-fid", "numericValue");
            Double cls = extractMetricValue(audits, "cumulative-layout-shift", "numericValue");
            
            // Extract performance score
            Integer score = extractPerformanceScore(categories);
            
            // Extract screenshot
            String screenshotB64 = extractScreenshot(lighthouseResult);
            
            return LookupDocument.WebVitalsData.builder()
                    .lcp(lcp != null ? lcp / 1000.0 : 0.0) // Convert to seconds
                    .fid(fid != null ? fid / 1000.0 : 0.0) // Convert to seconds
                    .cls(cls != null ? cls : 0.0)
                    .score(score != null ? score : 0)
                    .screenshotB64(screenshotB64)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error mapping PageSpeed response: {}", e.getMessage());
            return createDefaultWebVitalsData();
        }
    }
    
    @SuppressWarnings("unchecked")
    private Double extractMetricValue(Map<String, Object> audits, String metricName, String valueKey) {
        try {
            Map<String, Object> metric = (Map<String, Object>) audits.get(metricName);
            if (metric == null) return null;
            
            Object value = metric.get(valueKey);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return null;
        } catch (Exception e) {
            log.error("Error extracting metric {}: {}", metricName, e.getMessage());
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private Integer extractPerformanceScore(Map<String, Object> categories) {
        try {
            Map<String, Object> performance = (Map<String, Object>) categories.get("performance");
            if (performance == null) return null;
            
            Object score = performance.get("score");
            if (score instanceof Number) {
                return (int) (((Number) score).doubleValue() * 100);
            }
            return null;
        } catch (Exception e) {
            log.error("Error extracting performance score: {}", e.getMessage());
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private String extractScreenshot(Map<String, Object> lighthouseResult) {
        try {
            Map<String, Object> audits = (Map<String, Object>) lighthouseResult.get("audits");
            Map<String, Object> screenshot = (Map<String, Object>) audits.get("final-screenshot");
            if (screenshot == null) return null;
            
            String data = (String) screenshot.get("data");
            if (data != null && data.startsWith("data:image/png;base64,")) {
                return data.substring("data:image/png;base64,".length());
            }
            return data;
        } catch (Exception e) {
            log.error("Error extracting screenshot: {}", e.getMessage());
            return null;
        }
    }
    
    private LookupDocument.WebVitalsData createDefaultWebVitalsData() {
        return LookupDocument.WebVitalsData.builder()
                .lcp(0.0)
                .fid(0.0)
                .cls(0.0)
                .score(0)
                .screenshotB64(null)
                .build();
    }
}
