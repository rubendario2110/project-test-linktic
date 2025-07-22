package com.testbackend.products.dto;

import com.testbackend.products.domain.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(UUID id, String name, BigDecimal price, String description, Instant createdAt) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getDescription(), p.getCreatedAt());
    }
}
