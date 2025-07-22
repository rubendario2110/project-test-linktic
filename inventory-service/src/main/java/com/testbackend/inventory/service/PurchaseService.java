package com.testbackend.inventory.service;

import com.testbackend.inventory.client.ProductClient;
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
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepo;
    private final InventoryRepository inventoryRepo;
    private final ProductClient productClient;

    @Transactional
    public PurchaseResponse purchase(PurchaseRequest req) {
        Map<String, Object> productMap = productClient.get(req.productId());
        if (productMap.get("data") == null) {
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
                .build();
        purchaseRepo.save(purchase);

        return new PurchaseResponse(purchase.getId(), purchase.getProductId(), purchase.getQuantity(),
                purchase.getUnitPrice(), purchase.getTotalPrice(), purchase.getCreatedAt());
    }
}
