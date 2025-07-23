package com.testbackend.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testbackend.inventory.controller.PurchaseController;
import com.testbackend.inventory.dto.PurchaseRequest;
import com.testbackend.inventory.dto.PurchaseResponse;
import com.testbackend.inventory.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class PurchaseControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private PurchaseService purchaseService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID productId;
    private UUID purchaseId;
    private PurchaseRequest purchaseRequest;
    private PurchaseResponse purchaseResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
        productId = UUID.randomUUID();
        purchaseId = UUID.randomUUID();
        purchaseRequest = new PurchaseRequest(productId, 2);
        purchaseResponse = new PurchaseResponse(purchaseId, productId, 2, new BigDecimal("99.99"), new BigDecimal("199.98"), Instant.now());
    }

    @Test
    void purchase_WhenValidRequest_ShouldReturnPurchaseResponse() throws Exception {
        // Given
        when(purchaseService.purchase(any(PurchaseRequest.class))).thenReturn(purchaseResponse);

        // When & Then
        mockMvc.perform(post("/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("purchase"))
                .andExpect(jsonPath("$.data.id").value(purchaseId.toString()))
                .andExpect(jsonPath("$.data.attributes.productId").value(productId.toString()))
                .andExpect(jsonPath("$.data.attributes.quantity").value(2))
                .andExpect(jsonPath("$.data.attributes.unitPrice").value(99.99))
                .andExpect(jsonPath("$.data.attributes.totalPrice").value(199.98))
                .andExpect(jsonPath("$.data.attributes.createdAt").exists());
    }

    @Test
    void purchase_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        PurchaseRequest invalidRequest = new PurchaseRequest(productId, -1);

        // When & Then
        mockMvc.perform(post("/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void purchase_WithZeroQuantity_ShouldReturnBadRequest() throws Exception {
        // Given
        PurchaseRequest zeroQuantityRequest = new PurchaseRequest(productId, 0);

        // When & Then
        mockMvc.perform(post("/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zeroQuantityRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void purchase_WithLargeQuantity_ShouldReturnPurchaseResponse() throws Exception {
        // Given
        PurchaseRequest largeQuantityRequest = new PurchaseRequest(productId, 100);
        PurchaseResponse largeQuantityResponse = new PurchaseResponse(
                purchaseId, productId, 100, new BigDecimal("99.99"), new BigDecimal("9999.00"), Instant.now()
        );
        
        when(purchaseService.purchase(any(PurchaseRequest.class))).thenReturn(largeQuantityResponse);

        // When & Then
        mockMvc.perform(post("/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(largeQuantityRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.quantity").value(100))
                .andExpect(jsonPath("$.data.attributes.totalPrice").value(9999.00));
    }
} 