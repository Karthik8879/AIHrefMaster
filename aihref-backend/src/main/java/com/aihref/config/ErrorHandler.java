package com.aihref.config;

import com.aihref.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex, ServerWebExchange exchange) {
        
        log.error("Illegal argument exception: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .error("Bad Request")
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }
    
    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleRuntimeException(
            RuntimeException ex, ServerWebExchange exchange) {
        
        log.error("Runtime exception: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .timestamp(Instant.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
    
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {
        
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .timestamp(Instant.now())
                .path(exchange.getRequest().getPath().value())
                .build();
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
}
