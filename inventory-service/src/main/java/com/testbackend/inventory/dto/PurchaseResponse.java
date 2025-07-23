package com.testbackend.inventory.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PurchaseResponse(UUID id, UUID productId, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice, Instant createdAt) {
}
