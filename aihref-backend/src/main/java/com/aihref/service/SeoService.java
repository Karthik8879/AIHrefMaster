package com.aihref.service;

import com.aihref.model.LookupDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeoService {
    
    private final WebClient pageSpeedClient; // Reuse WebClient for HTML scraping
    
    @Cacheable(value = "seo", key = "#url")
    public Mono<LookupDocument.SeoData> getSeoData(String url) {
        log.info("Fetching SEO data for URL: {}", url);
        
        return pageSpeedClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseHtmlForSeo)
                .doOnSuccess(data -> log.info("Successfully fetched SEO data for {}", url))
                .doOnError(error -> log.error("Failed to fetch SEO data for {}: {}", url, error.getMessage()))
                .onErrorReturn(createDefaultSeoData());
    }
    
    private LookupDocument.SeoData parseHtmlForSeo(String html) {
        try {
            String title = extractTitle(html);
            String description = extractDescription(html);
            String h1 = extractH1(html);
            Integer wordCount = countWords(html);
            Double readability = calculateReadability(html);
            Boolean canonical = checkCanonical(html);
            
            return LookupDocument.SeoData.builder()
                    .title(title)
                    .description(description)
                    .h1(h1)
                    .wordCount(wordCount)
                    .readability(readability)
                    .canonical(canonical)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error parsing HTML for SEO data: {}", e.getMessage());
            return createDefaultSeoData();
        }
    }
    
    private String extractTitle(String html) {
        Pattern pattern = Pattern.compile("<title[^>]*>(.*?)</title>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "No title found";
    }
    
    private String extractDescription(String html) {
        Pattern pattern = Pattern.compile("<meta[^>]*name=[\"']description[\"'][^>]*content=[\"']([^\"']*)[\"'][^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "No description found";
    }
    
    private String extractH1(String html) {
        Pattern pattern = Pattern.compile("<h1[^>]*>(.*?)</h1>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).replaceAll("<[^>]+>", "").trim();
        }
        return "No H1 found";
    }
    
    private Integer countWords(String html) {
        // Remove HTML tags and count words
        String text = html.replaceAll("<[^>]+>", " ");
        text = text.replaceAll("\\s+", " ").trim();
        
        if (text.isEmpty()) return 0;
        
        String[] words = text.split("\\s+");
        return words.length;
    }
    
    private Double calculateReadability(String html) {
        // Simplified Flesch Reading Ease Score
        String text = html.replaceAll("<[^>]+>", " ");
        text = text.replaceAll("\\s+", " ").trim();
        
        if (text.isEmpty()) return 0.0;
        
        String[] sentences = text.split("[.!?]+");
        String[] words = text.split("\\s+");
        
        int totalWords = words.length;
        int totalSentences = sentences.length;
        
        if (totalSentences == 0) return 0.0;
        
        // Count syllables (simplified)
        int totalSyllables = 0;
        for (String word : words) {
            totalSyllables += countSyllables(word);
        }
        
        // Flesch Reading Ease Score
        double score = 206.835 - (1.015 * (totalWords / (double) totalSentences)) - (84.6 * (totalSyllables / (double) totalWords));
        
        return Math.max(0.0, Math.min(100.0, score));
    }
    
    private int countSyllables(String word) {
        if (word == null || word.isEmpty()) return 0;
        
        word = word.toLowerCase().replaceAll("[^a-z]", "");
        if (word.isEmpty()) return 0;
        
        int syllables = 0;
        boolean previousVowel = false;
        
        for (int i = 0; i < word.length(); i++) {
            boolean isVowel = isVowel(word.charAt(i));
            if (isVowel && !previousVowel) {
                syllables++;
            }
            previousVowel = isVowel;
        }
        
        // Handle silent 'e'
        if (word.endsWith("e") && syllables > 1) {
            syllables--;
        }
        
        return Math.max(1, syllables);
    }
    
    private boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y';
    }
    
    private Boolean checkCanonical(String html) {
        Pattern pattern = Pattern.compile("<link[^>]*rel=[\"']canonical[\"'][^>]*href=[\"']([^\"']*)[\"'][^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        return matcher.find();
    }
    
    private LookupDocument.SeoData createDefaultSeoData() {
        return LookupDocument.SeoData.builder()
                .title("No title found")
                .description("No description found")
                .h1("No H1 found")
                .wordCount(0)
                .readability(0.0)
                .canonical(false)
                .build();
    }
}
