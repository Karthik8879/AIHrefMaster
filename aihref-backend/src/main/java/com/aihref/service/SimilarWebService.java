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
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarWebService {
    
    private final WebClient similarWebClient;
    
    @Value("${external.apis.similarweb.base-url}")
    private String baseUrl;
    
    @Cacheable(value = "similarweb", key = "#url")
    public Mono<LookupDocument.TrafficData> getTrafficData(String url) {
        try {
            String host = new URL(url).getHost();
            log.info("Fetching traffic data for host: {}", host);
            
            return similarWebClient
                    .get()
                    .uri(baseUrl + "/website/{host}/total-traffic-and-engagement", host)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(this::mapToTrafficData)
                    .doOnSuccess(data -> log.info("Successfully fetched traffic data for {}", host))
                    .doOnError(error -> log.error("Failed to fetch traffic data for {}: {}", host, error.getMessage()))
                    .onErrorReturn(createDefaultTrafficData());
                    
        } catch (Exception e) {
            log.error("Error parsing URL {}: {}", url, e.getMessage());
            return Mono.just(createDefaultTrafficData());
        }
    }
    
    private LookupDocument.TrafficData mapToTrafficData(Map<String, Object> response) {
        try {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data == null) {
                return createDefaultTrafficData();
            }
            
            return LookupDocument.TrafficData.builder()
                    .visits(getLongValue(data, "visits"))
                    .rank(getLongValue(data, "global_rank"))
                    .bounce(getDoubleValue(data, "bounce_rate"))
                    .avgDuration(getLongValue(data, "avg_visit_duration"))
                    .countries(extractCountries(data))
                    .devices(extractDevices(data))
                    .referrers(extractReferrers(data))
                    .searchShare(getDoubleValue(data, "search_share"))
                    .socialShare(getDoubleValue(data, "social_share"))
                    .build();
                    
        } catch (Exception e) {
            log.error("Error mapping SimilarWeb response: {}", e.getMessage());
            return createDefaultTrafficData();
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<LookupDocument.CountryData> extractCountries(Map<String, Object> data) {
        try {
            Map<String, Object> countries = (Map<String, Object>) data.get("countries");
            if (countries == null) return List.of();
            
            return countries.entrySet().stream()
                    .limit(10)
                    .map(entry -> LookupDocument.CountryData.builder()
                            .country(entry.getKey())
                            .share(getDoubleValue((Map<String, Object>) entry.getValue(), "share"))
                            .visits(getLongValue((Map<String, Object>) entry.getValue(), "visits"))
                            .build())
                    .toList();
        } catch (Exception e) {
            log.error("Error extracting countries: {}", e.getMessage());
            return List.of();
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<LookupDocument.DeviceData> extractDevices(Map<String, Object> data) {
        try {
            Map<String, Object> devices = (Map<String, Object>) data.get("devices");
            if (devices == null) return List.of();
            
            return devices.entrySet().stream()
                    .map(entry -> LookupDocument.DeviceData.builder()
                            .device(entry.getKey())
                            .share(getDoubleValue((Map<String, Object>) entry.getValue(), "share"))
                            .build())
                    .toList();
        } catch (Exception e) {
            log.error("Error extracting devices: {}", e.getMessage());
            return List.of();
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<LookupDocument.ReferrerData> extractReferrers(Map<String, Object> data) {
        try {
            Map<String, Object> referrers = (Map<String, Object>) data.get("referrers");
            if (referrers == null) return List.of();
            
            return referrers.entrySet().stream()
                    .limit(10)
                    .map(entry -> LookupDocument.ReferrerData.builder()
                            .site(entry.getKey())
                            .share(getDoubleValue((Map<String, Object>) entry.getValue(), "share"))
                            .build())
                    .toList();
        } catch (Exception e) {
            log.error("Error extracting referrers: {}", e.getMessage());
            return List.of();
        }
    }
    
    private Long getLongValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }
    
    private Double getDoubleValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
    
    private LookupDocument.TrafficData createDefaultTrafficData() {
        return LookupDocument.TrafficData.builder()
                .visits(0L)
                .rank(0L)
                .bounce(0.0)
                .avgDuration(0L)
                .countries(List.of())
                .devices(List.of())
                .referrers(List.of())
                .searchShare(0.0)
                .socialShare(0.0)
                .build();
    }
}
