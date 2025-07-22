package com.testbackend.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(@NotBlank String name, @NotNull @Positive BigDecimal price, String description) {
}
