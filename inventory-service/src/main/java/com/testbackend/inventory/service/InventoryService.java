package com.testbackend.inventory.service;

import com.testbackend.inventory.domain.Inventory;
import com.testbackend.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository repo;

    @Transactional
    public Inventory increase(UUID productId, int delta) {
        Inventory inv = repo.findById(productId)
                .orElse(Inventory.builder().productId(productId).quantity(0).build());
        inv.setQuantity(inv.getQuantity() + delta);
        return repo.save(inv);
    }

    public Inventory get(UUID productId) {
        return repo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "INVENTORY_NOT_FOUND"));
    }
}
