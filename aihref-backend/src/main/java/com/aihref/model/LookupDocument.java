package com.aihref.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "lookups")
public class LookupDocument {
    
    @Id
    private String id;
    
    private String url;
    private Instant requestedAt;
    private Instant expiresAt;
    
    private TrafficData traffic;
    private WebVitalsData webVitals;
    private SeoData seo;
    private TechData tech;
    private LiveData live;
    private ThreatData threat;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrafficData {
        private Long visits;
        private Long rank;
        private Double bounce;
        private Long avgDuration;
        private List<CountryData> countries;
        private List<DeviceData> devices;
        private List<ReferrerData> referrers;
        private Double searchShare;
        private Double socialShare;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryData {
        private String country;
        private Double share;
        private Long visits;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceData {
        private String device;
        private Double share;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferrerData {
        private String site;
        private Double share;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebVitalsData {
        private Double lcp;
        private Double fid;
        private Double cls;
        private Integer score;
        private String screenshotB64;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeoData {
        private String title;
        private String description;
        private String h1;
        private Integer wordCount;
        private Double readability;
        private Boolean canonical;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TechData {
        private Boolean ipv6;
        private Boolean http2;
        private Integer sslDaysLeft;
        private String securityGrade;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LiveData {
        private Integer trafficIndex;
        private Instant lastUpdated;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThreatData {
        private Boolean safeBrowsing;
    }
}
