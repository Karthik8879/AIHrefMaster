package com.aihref.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {
    
    @Value("${external.apis.similarweb.timeout:10000}")
    private int similarWebTimeout;
    
    @Value("${external.apis.pagespeed.timeout:10000}")
    private int pageSpeedTimeout;
    
    @Value("${external.apis.rdap.timeout:5000}")
    private int rdapTimeout;
    
    @Value("${external.apis.cloudflare.timeout:5000}")
    private int cloudflareTimeout;
    
    @Value("${external.apis.safe-browsing.timeout:5000}")
    private int safeBrowsingTimeout;
    
    @Bean
    public WebClient similarWebClient() {
        return createWebClient(similarWebTimeout);
    }
    
    @Bean
    public WebClient pageSpeedClient() {
        return createWebClient(pageSpeedTimeout);
    }
    
    @Bean
    public WebClient rdapClient() {
        return createWebClient(rdapTimeout);
    }
    
    @Bean
    public WebClient cloudflareClient() {
        return createWebClient(cloudflareTimeout);
    }
    
    @Bean
    public WebClient safeBrowsingClient() {
        return createWebClient(safeBrowsingTimeout);
    }
    
    private WebClient createWebClient(int timeout) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .responseTimeout(Duration.ofMillis(timeout))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS)));
        
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
