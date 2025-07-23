package com.testbackend.inventory.service;

import com.testbackend.inventory.domain.Inventory;
import com.testbackend.inventory.domain.Purchase;
import com.testbackend.inventory.dto.PurchaseRequest;
import com.testbackend.inventory.dto.PurchaseResponse;
import com.testbackend.inventory.repository.InventoryRepository;
import com.testbackend.inventory.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepo;
    private final InventoryRepository inventoryRepo;
    private final RestTemplate restTemplate;

    @Transactional
    public PurchaseResponse purchase(PurchaseRequest req) {
        String url = "http://products-service/products/" + req.productId();
        Map<String, Object> productMap = restTemplate.getForObject(url, Map.class);
        
        if (productMap == null || productMap.get("data") == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND");
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> attributes = (Map<String, Object>) ((Map<String, Object>) productMap.get("data")).get("attributes");
        BigDecimal price = new BigDecimal(attributes.get("price").toString());

        Inventory inv = inventoryRepo.findById(req.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "INVENTORY_NOT_FOUND"));
        
        if (inv.getQuantity() < req.quantity()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_STOCK");
        }
        
        inv.setQuantity(inv.getQuantity() - req.quantity());
        inventoryRepo.save(inv);

        Purchase purchase = Purchase.builder()
                .productId(req.productId())
                .quantity(req.quantity())
                .unitPrice(price)
                .totalPrice(price.multiply(BigDecimal.valueOf(req.quantity())))
                .createdAt(Instant.now())
                .build();
        purchase = purchaseRepo.save(purchase);

        return new PurchaseResponse(purchase.getId(), purchase.getProductId(), purchase.getQuantity(),
                purchase.getUnitPrice(), purchase.getTotalPrice(), purchase.getCreatedAt());
    }
}
