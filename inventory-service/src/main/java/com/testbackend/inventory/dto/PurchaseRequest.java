package com.testbackend.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record PurchaseRequest(@NotNull UUID productId, @NotNull @Positive Integer quantity) {
}
