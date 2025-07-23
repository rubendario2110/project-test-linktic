package com.testbackend.inventory;

import com.testbackend.inventory.domain.Inventory;
import com.testbackend.inventory.dto.PurchaseRequest;
import com.testbackend.inventory.dto.PurchaseResponse;
import com.testbackend.inventory.repository.InventoryRepository;
import com.testbackend.inventory.repository.PurchaseRepository;
import com.testbackend.inventory.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PurchaseService purchaseService;

    private UUID productId;
    private Inventory testInventory;
    private PurchaseRequest testPurchaseRequest;
    private Map<String, Object> mockProductResponse;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        
        testInventory = Inventory.builder()
                .productId(productId)
                .quantity(10)
                .build();

        testPurchaseRequest = new PurchaseRequest(productId, 2);

        mockProductResponse = Map.of(
                "data", Map.of(
                        "attributes", Map.of("price", 99.99)
                )
        );
    }

    @Test
    void purchase_WhenValidRequest_ShouldReturnPurchaseResponse() {
        // Given
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(mockProductResponse);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);
        
        // Create a mock Purchase object for the return value
        com.testbackend.inventory.domain.Purchase mockPurchase = com.testbackend.inventory.domain.Purchase.builder()
                .id(UUID.randomUUID())
                .productId(productId)
                .quantity(2)
                .unitPrice(new BigDecimal("99.99"))
                .totalPrice(new BigDecimal("199.98"))
                .createdAt(Instant.now())
                .build();
        when(purchaseRepository.save(any())).thenReturn(mockPurchase);

        // When
        PurchaseResponse result = purchaseService.purchase(testPurchaseRequest);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.productId());
        assertEquals(2, result.quantity());
        assertEquals(new BigDecimal("99.99"), result.unitPrice());
        assertEquals(new BigDecimal("199.98"), result.totalPrice());
    }

    @Test
    void purchase_WhenProductNotFound_ShouldThrowException() {
        // Given
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> purchaseService.purchase(testPurchaseRequest));
    }

    @Test
    void purchase_WhenInsufficientStock_ShouldThrowException() {
        // Given
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(mockProductResponse);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(testInventory));

        PurchaseRequest invalidRequest = new PurchaseRequest(productId, 15);

        // When & Then
        assertThrows(RuntimeException.class, () -> purchaseService.purchase(invalidRequest));
    }
} 