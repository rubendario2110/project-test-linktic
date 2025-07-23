package com.testbackend.inventory;

import com.testbackend.inventory.domain.Inventory;
import com.testbackend.inventory.repository.InventoryRepository;
import com.testbackend.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private UUID productId;
    private Inventory existingInventory;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        existingInventory = Inventory.builder()
                .productId(productId)
                .quantity(10)
                .build();
    }

    @Test
    void increase_WhenInventoryExists_ShouldIncreaseQuantity() {
        // Given
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(existingInventory);

        // When
        Inventory result = inventoryService.increase(productId, 5);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(15, result.getQuantity());
    }

    @Test
    void increase_WhenInventoryDoesNotExist_ShouldCreateNewInventory() {
        // Given
        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(existingInventory);

        // When
        Inventory result = inventoryService.increase(productId, 10);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(10, result.getQuantity());
    }

    @Test
    void increase_WithNegativeDelta_ShouldDecreaseQuantity() {
        // Given
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(existingInventory);

        // When
        Inventory result = inventoryService.increase(productId, -3);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(7, result.getQuantity());
    }

    @Test
    void increase_WithZeroDelta_ShouldReturnSameQuantity() {
        // Given
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(existingInventory);

        // When
        Inventory result = inventoryService.increase(productId, 0);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(10, result.getQuantity());
    }

    @Test
    void get_WhenInventoryExists_ShouldReturnInventory() {
        // Given
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(existingInventory));

        // When
        Inventory result = inventoryService.get(productId);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(10, result.getQuantity());
    }

    @Test
    void get_WhenInventoryDoesNotExist_ShouldThrowException() {
        // Given
        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            inventoryService.get(productId);
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("INVENTORY_NOT_FOUND", exception.getReason());
    }

    @Test
    void increase_WithLargeQuantity_ShouldHandleCorrectly() {
        // Given
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(existingInventory);

        // When
        Inventory result = inventoryService.increase(productId, 1000);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(1010, result.getQuantity());
    }

    @Test
    void increase_WithNegativeQuantityExceedingCurrent_ShouldHandleCorrectly() {
        // Given
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(existingInventory);

        // When
        Inventory result = inventoryService.increase(productId, -15);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(-5, result.getQuantity()); // This might be a business logic issue
    }
} 