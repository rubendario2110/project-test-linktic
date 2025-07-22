package com.testbackend.inventory.controller;

import com.testbackend.inventory.dto.PurchaseRequest;
import com.testbackend.inventory.dto.PurchaseResponse;
import com.testbackend.inventory.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService service;

    @PostMapping
    public Map<String, Object> purchase(@Valid @RequestBody PurchaseRequest req) {
        PurchaseResponse pr = service.purchase(req);
        return Map.of("data", Map.of("type", "purchase", "id", pr.id(),
                "attributes", Map.of(
                        "productId", pr.productId(),
                        "quantity", pr.quantity(),
                        "unitPrice", pr.unitPrice(),
                        "totalPrice", pr.totalPrice(),
                        "createdAt", pr.createdAt())));
    }
}
