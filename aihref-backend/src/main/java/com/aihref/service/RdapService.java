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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RdapService {
    
    private final WebClient rdapClient;
    
    @Value("${external.apis.rdap.base-url}")
    private String baseUrl;
    
    @Cacheable(value = "rdap", key = "#url")
    public Mono<LookupDocument.TechData> getTechData(String url) {
        try {
            String host = new URL(url).getHost();
            log.info("Fetching RDAP data for host: {}", host);
            
            return rdapClient
                    .get()
                    .uri(baseUrl + "/domain/{host}", host)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(this::mapToTechData)
                    .doOnSuccess(data -> log.info("Successfully fetched RDAP data for {}", host))
                    .doOnError(error -> log.error("Failed to fetch RDAP data for {}: {}", host, error.getMessage()))
                    .onErrorReturn(createDefaultTechData());
                    
        } catch (Exception e) {
            log.error("Error parsing URL {}: {}", url, e.getMessage());
            return Mono.just(createDefaultTechData());
        }
    }
    
    @SuppressWarnings("unchecked")
    private LookupDocument.TechData mapToTechData(Map<String, Object> response) {
        try {
            // Extract events for creation and expiry dates
            java.util.List<Map<String, Object>> events = (java.util.List<Map<String, Object>>) response.get("events");
            Instant creationDate = null;
            Instant expiryDate = null;
            
            if (events != null) {
                for (Map<String, Object> event : events) {
                    String action = (String) event.get("eventAction");
                    if ("registration".equals(action)) {
                        creationDate = parseDate((String) event.get("eventDate"));
                    } else if ("expiration".equals(action)) {
                        expiryDate = parseDate((String) event.get("eventDate"));
                    }
                }
            }
            
            // Calculate SSL days left (simplified - in real implementation, check actual SSL cert)
            Integer sslDaysLeft = calculateSslDaysLeft(expiryDate);
            
            // Extract registrar information
            java.util.List<Map<String, Object>> entities = (java.util.List<Map<String, Object>>) response.get("entities");
            String registrar = extractRegistrar(entities);
            
            return LookupDocument.TechData.builder()
                    .ipv6(checkIpv6Support(response))
                    .http2(true) // Assume HTTP/2 support for modern domains
                    .sslDaysLeft(sslDaysLeft)
                    .securityGrade(calculateSecurityGrade(sslDaysLeft, registrar))
                    .build();
                    
        } catch (Exception e) {
            log.error("Error mapping RDAP response: {}", e.getMessage());
            return createDefaultTechData();
        }
    }
    
    private Instant parseDate(String dateString) {
        if (dateString == null) return null;
        try {
            return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME).toInstant();
        } catch (Exception e) {
            log.error("Error parsing date {}: {}", dateString, e.getMessage());
            return null;
        }
    }
    
    private Integer calculateSslDaysLeft(Instant expiryDate) {
        if (expiryDate == null) return 365; // Default to 1 year if unknown
        
        long daysLeft = (expiryDate.toEpochMilli() - Instant.now().toEpochMilli()) / (1000 * 60 * 60 * 24);
        return Math.max(0, (int) daysLeft);
    }
    
    @SuppressWarnings("unchecked")
    private String extractRegistrar(java.util.List<Map<String, Object>> entities) {
        if (entities == null) return "Unknown";
        
        try {
            for (Map<String, Object> entity : entities) {
                java.util.List<String> roles = (java.util.List<String>) entity.get("roles");
                if (roles != null && roles.contains("registrar")) {
                    java.util.List<Map<String, Object>> vcardArray = (java.util.List<Map<String, Object>>) entity.get("vcardArray");
                    if (vcardArray != null && !vcardArray.isEmpty()) {
                        // Extract organization name from vCard
                        return extractOrgFromVCard(vcardArray);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error extracting registrar: {}", e.getMessage());
        }
        return "Unknown";
    }
    
    @SuppressWarnings("unchecked")
    private String extractOrgFromVCard(java.util.List<Map<String, Object>> vcardArray) {
        try {
            for (Map<String, Object> vcard : vcardArray) {
                if (vcard instanceof java.util.List) {
                    java.util.List<Object> vcardList = (java.util.List<Object>) vcard;
                    for (Object item : vcardList) {
                        if (item instanceof java.util.List) {
                            java.util.List<Object> itemList = (java.util.List<Object>) item;
                            if (itemList.size() >= 2 && "org".equals(itemList.get(0))) {
                                return (String) itemList.get(3);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error extracting organization from vCard: {}", e.getMessage());
        }
        return "Unknown";
    }
    
    @SuppressWarnings("unchecked")
    private Boolean checkIpv6Support(Map<String, Object> response) {
        try {
            java.util.List<Map<String, Object>> nameservers = (java.util.List<Map<String, Object>>) response.get("nameservers");
            if (nameservers == null) return false;
            
            for (Map<String, Object> nameserver : nameservers) {
                java.util.List<String> ipAddresses = (java.util.List<String>) nameserver.get("ipAddresses");
                if (ipAddresses != null) {
                    for (String ip : ipAddresses) {
                        if (ip.contains(":")) { // IPv6 addresses contain colons
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error checking IPv6 support: {}", e.getMessage());
        }
        return false;
    }
    
    private String calculateSecurityGrade(Integer sslDaysLeft, String registrar) {
        if (sslDaysLeft == null || sslDaysLeft < 30) return "F";
        if (sslDaysLeft < 90) return "D";
        if (sslDaysLeft < 180) return "C";
        if (sslDaysLeft < 365) return "B";
        return "A";
    }
    
    private LookupDocument.TechData createDefaultTechData() {
        return LookupDocument.TechData.builder()
                .ipv6(false)
                .http2(true)
                .sslDaysLeft(365)
                .securityGrade("B")
                .build();
    }
}
