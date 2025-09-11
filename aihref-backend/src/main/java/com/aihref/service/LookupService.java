package com.aihref.service;

import com.aihref.dto.LookupRequest;
import com.aihref.dto.LookupResponse;
import com.aihref.mapper.LookupMapper;
import com.aihref.model.LookupDocument;
import com.aihref.repository.LookupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LookupService {
    
    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;
    private final SimilarWebService similarWebService;
    private final PageSpeedService pageSpeedService;
    private final RdapService rdapService;
    private final CloudflareRadarService cloudflareRadarService;
    private final SeoService seoService;
    
    public Mono<String> analyseUrl(LookupRequest request) {
        String url = normalizeUrl(request.getUrl());
        String lookupId = UUID.randomUUID().toString();
        
        log.info("Starting analysis for URL: {} with lookup ID: {}", url, lookupId);
        
        return lookupRepository.existsByUrl(url)
                .flatMap(exists -> {
                    if (exists) {
                        return lookupRepository.findByUrl(url)
                                .map(LookupDocument::getId);
                    } else {
                        return createNewLookup(url, lookupId);
                    }
                });
    }
    
    @Cacheable(value = "lookup", key = "#lookupId")
    public Mono<LookupResponse> getLookup(String lookupId) {
        log.info("Fetching lookup data for ID: {}", lookupId);
        
        return lookupRepository.findById(lookupId)
                .map(lookupMapper::toResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Lookup not found")));
    }
    
    private Mono<String> createNewLookup(String url, String lookupId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(30, ChronoUnit.DAYS);
        
        // Create initial document
        LookupDocument document = LookupDocument.builder()
                .id(lookupId)
                .url(url)
                .requestedAt(now)
                .expiresAt(expiresAt)
                .build();
        
        // Save initial document
        return lookupRepository.save(document)
                .then(fetchAllData(url, lookupId))
                .thenReturn(lookupId);
    }
    
    private Mono<Void> fetchAllData(String url, String lookupId) {
        log.info("Fetching all data for URL: {} with lookup ID: {}", url, lookupId);
        
        return Mono.when(
                fetchTrafficData(url, lookupId),
                fetchWebVitalsData(url, lookupId),
                fetchTechData(url, lookupId),
                fetchLiveData(url, lookupId),
                fetchSeoData(url, lookupId)
        ).then();
    }
    
    private Mono<Void> fetchTrafficData(String url, String lookupId) {
        return similarWebService.getTrafficData(url)
                .flatMap(trafficData -> 
                    lookupRepository.findById(lookupId)
                            .map(doc -> {
                                doc.setTraffic(trafficData);
                                return doc;
                            })
                            .flatMap(lookupRepository::save)
                )
                .then();
    }
    
    private Mono<Void> fetchWebVitalsData(String url, String lookupId) {
        return pageSpeedService.getWebVitalsData(url)
                .flatMap(webVitalsData -> 
                    lookupRepository.findById(lookupId)
                            .map(doc -> {
                                doc.setWebVitals(webVitalsData);
                                return doc;
                            })
                            .flatMap(lookupRepository::save)
                )
                .then();
    }
    
    private Mono<Void> fetchTechData(String url, String lookupId) {
        return rdapService.getTechData(url)
                .flatMap(techData -> 
                    lookupRepository.findById(lookupId)
                            .map(doc -> {
                                doc.setTech(techData);
                                return doc;
                            })
                            .flatMap(lookupRepository::save)
                )
                .then();
    }
    
    private Mono<Void> fetchLiveData(String url, String lookupId) {
        return cloudflareRadarService.getLiveData(url)
                .flatMap(liveData -> 
                    lookupRepository.findById(lookupId)
                            .map(doc -> {
                                doc.setLive(liveData);
                                return doc;
                            })
                            .flatMap(lookupRepository::save)
                )
                .then();
    }
    
    private Mono<Void> fetchSeoData(String url, String lookupId) {
        return seoService.getSeoData(url)
                .flatMap(seoData -> 
                    lookupRepository.findById(lookupId)
                            .map(doc -> {
                                doc.setSeo(seoData);
                                return doc;
                            })
                            .flatMap(lookupRepository::save)
                )
                .then();
    }
    
    private String normalizeUrl(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            new URL(url); // Validate URL
            return url;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL format: " + url);
        }
    }
}
