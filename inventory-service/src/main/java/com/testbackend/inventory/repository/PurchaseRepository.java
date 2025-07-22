package com.testbackend.inventory.repository;

import com.testbackend.inventory.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
}
