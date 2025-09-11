package com.aihref.controller;

import com.aihref.dto.AnalyseResponse;
import com.aihref.dto.LookupRequest;
import com.aihref.dto.LookupResponse;
import com.aihref.service.LookupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Lookup", description = "Website analysis and lookup operations")
public class LookupController {
    
    private final LookupService lookupService;
    
    @PostMapping(value = "/analyse", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Analyse a website", description = "Start analysis of a website URL and return lookup ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Analysis started successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid URL format"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public Mono<ResponseEntity<AnalyseResponse>> analyseUrl(@Valid @RequestBody LookupRequest request) {
        log.info("Received analysis request for URL: {}", request.getUrl());
        
        return lookupService.analyseUrl(request)
                .map(lookupId -> {
                    AnalyseResponse response = AnalyseResponse.builder()
                            .lookupId(lookupId)
                            .message("Analysis started successfully. Use the lookup ID to retrieve results.")
                            .build();
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
                })
                .doOnSuccess(response -> log.info("Analysis request processed successfully"))
                .doOnError(error -> log.error("Error processing analysis request: {}", error.getMessage()));
    }
    
    @GetMapping(value = "/lookup/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get lookup results", description = "Retrieve analysis results by lookup ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lookup results retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Lookup not found"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public Mono<ResponseEntity<LookupResponse>> getLookup(
            @Parameter(description = "Lookup ID", required = true)
            @PathVariable String id) {
        
        log.info("Retrieving lookup results for ID: {}", id);
        
        return lookupService.getLookup(id)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Lookup results retrieved successfully for ID: {}", id))
                .doOnError(error -> log.error("Error retrieving lookup results for ID {}: {}", id, error.getMessage()));
    }
    
    @GetMapping(value = "/export/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Export lookup data", description = "Export lookup results in CSV or JSON format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Export data retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Lookup not found"),
            @ApiResponse(responseCode = "400", description = "Invalid export format"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public Mono<ResponseEntity<String>> exportLookup(
            @Parameter(description = "Lookup ID", required = true)
            @PathVariable String id,
            @Parameter(description = "Export format (csv or json)", required = true)
            @RequestParam(defaultValue = "json") String type) {
        
        log.info("Exporting lookup data for ID: {} in format: {}", id, type);
        
        return lookupService.getLookup(id)
                .map(lookupData -> {
                    String exportData = generateExportData(lookupData, type);
                    String contentType = "csv".equalsIgnoreCase(type) ? "text/csv" : "application/json";
                    String filename = "lookup_" + id + "." + type;
                    
                    return ResponseEntity.ok()
                            .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                            .header("Content-Type", contentType)
                            .body(exportData);
                })
                .doOnSuccess(response -> log.info("Export data generated successfully for ID: {}", id))
                .doOnError(error -> log.error("Error generating export data for ID {}: {}", id, error.getMessage()));
    }
    
    private String generateExportData(LookupResponse lookupData, String type) {
        if ("csv".equalsIgnoreCase(type)) {
            return generateCsvExport(lookupData);
        } else {
            return generateJsonExport(lookupData);
        }
    }
    
    private String generateCsvExport(LookupResponse lookupData) {
        StringBuilder csv = new StringBuilder();
        csv.append("Metric,Value\n");
        csv.append("URL,").append(lookupData.getUrl()).append("\n");
        csv.append("Requested At,").append(lookupData.getRequestedAt()).append("\n");
        csv.append("Expires At,").append(lookupData.getExpiresAt()).append("\n");
        
        if (lookupData.getTraffic() != null) {
            csv.append("Estimated Visits/Month,").append(lookupData.getTraffic().getVisits()).append("\n");
            csv.append("Global Rank,").append(lookupData.getTraffic().getRank()).append("\n");
            csv.append("Bounce Rate,").append(lookupData.getTraffic().getBounce()).append("\n");
            csv.append("Avg Duration,").append(lookupData.getTraffic().getAvgDuration()).append("\n");
        }
        
        if (lookupData.getWebVitals() != null) {
            csv.append("LCP,").append(lookupData.getWebVitals().getLcp()).append("\n");
            csv.append("FID,").append(lookupData.getWebVitals().getFid()).append("\n");
            csv.append("CLS,").append(lookupData.getWebVitals().getCls()).append("\n");
            csv.append("Performance Score,").append(lookupData.getWebVitals().getScore()).append("\n");
        }
        
        if (lookupData.getSeo() != null) {
            csv.append("Title,").append(lookupData.getSeo().getTitle()).append("\n");
            csv.append("Description,").append(lookupData.getSeo().getDescription()).append("\n");
            csv.append("Word Count,").append(lookupData.getSeo().getWordCount()).append("\n");
            csv.append("Readability Score,").append(lookupData.getSeo().getReadability()).append("\n");
            csv.append("Has Canonical,").append(lookupData.getSeo().getCanonical()).append("\n");
        }
        
        if (lookupData.getTech() != null) {
            csv.append("IPv6 Support,").append(lookupData.getTech().getIpv6()).append("\n");
            csv.append("HTTP/2 Support,").append(lookupData.getTech().getHttp2()).append("\n");
            csv.append("SSL Days Left,").append(lookupData.getTech().getSslDaysLeft()).append("\n");
            csv.append("Security Grade,").append(lookupData.getTech().getSecurityGrade()).append("\n");
        }
        
        if (lookupData.getLive() != null) {
            csv.append("Traffic Index,").append(lookupData.getLive().getTrafficIndex()).append("\n");
            csv.append("Last Updated,").append(lookupData.getLive().getLastUpdated()).append("\n");
        }
        
        return csv.toString();
    }
    
    private String generateJsonExport(LookupResponse lookupData) {
        // Simple JSON export - in production, use a proper JSON library
        return "{\n" +
                "  \"id\": \"" + lookupData.getId() + "\",\n" +
                "  \"url\": \"" + lookupData.getUrl() + "\",\n" +
                "  \"requestedAt\": \"" + lookupData.getRequestedAt() + "\",\n" +
                "  \"expiresAt\": \"" + lookupData.getExpiresAt() + "\",\n" +
                "  \"traffic\": " + (lookupData.getTraffic() != null ? "{}" : "null") + ",\n" +
                "  \"webVitals\": " + (lookupData.getWebVitals() != null ? "{}" : "null") + ",\n" +
                "  \"seo\": " + (lookupData.getSeo() != null ? "{}" : "null") + ",\n" +
                "  \"tech\": " + (lookupData.getTech() != null ? "{}" : "null") + ",\n" +
                "  \"live\": " + (lookupData.getLive() != null ? "{}" : "null") + "\n" +
                "}";
    }
}
