package com.testbackend.gateway.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Fallback Controller for Circuit Breaker (optional)
 * This controller is used when circuit breaker is enabled
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/products")
    public Mono<ResponseEntity<Map<String, Object>>> productsFallback() {
        return Mono.just(ResponseEntity.status(503)
                .body(Map.of(
                        "error", "Products Service is temporarily unavailable",
                        "message", "Please try again later",
                        "timestamp", System.currentTimeMillis()
                )));
    }

    @GetMapping("/inventory")
    public Mono<ResponseEntity<Map<String, Object>>> inventoryFallback() {
        return Mono.just(ResponseEntity.status(503)
                .body(Map.of(
                        "error", "Inventory Service is temporarily unavailable",
                        "message", "Please try again later",
                        "timestamp", System.currentTimeMillis()
                )));
    }
} 