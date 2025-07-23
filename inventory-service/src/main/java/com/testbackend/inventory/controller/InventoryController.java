package com.testbackend.inventory.controller;

import com.testbackend.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService service;

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable UUID id) {
        var inv = service.get(id);
        return Map.of("data", Map.of("type", "inventory", "id", inv.getProductId(),
                "attributes", Map.of("quantity", inv.getQuantity())));
    }

    @PatchMapping("/{id}")
    public Map<String, Object> patch(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        int quantity = (Integer) ((Map<String, Object>) ((Map<String, Object>) body.get("data")).get("attributes")).get("quantity");
        var inv = service.increase(id, quantity);
        return Map.of("data", Map.of("type", "inventory", "id", inv.getProductId(),
                "attributes", Map.of("quantity", inv.getQuantity())));
    }
}
