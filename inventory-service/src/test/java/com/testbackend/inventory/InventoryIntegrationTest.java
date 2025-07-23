package com.testbackend.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testbackend.inventory.domain.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class InventoryIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private UUID productId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        productId = UUID.randomUUID();
    }

    @Test
    void contextLoads() {
        assert mockMvc != null;
    }

    @Test
    void getInventory_WhenInventoryExists_ShouldReturnInventory() throws Exception {
        // Given - First create inventory by increasing it
        Map<String, Object> createRequest = Map.of(
                "data", Map.of(
                        "attributes", Map.of("quantity", 10)
                )
        );

        mockMvc.perform(patch("/inventory/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // When & Then - Get the inventory
        mockMvc.perform(get("/inventory/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("inventory"))
                .andExpect(jsonPath("$.data.id").value(productId.toString()))
                .andExpect(jsonPath("$.data.attributes.quantity").value(10));
    }

    @Test
    void getInventory_WhenInventoryDoesNotExist_ShouldReturnNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/inventory/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchInventory_WhenValidRequest_ShouldIncreaseQuantity() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
                "data", Map.of(
                        "attributes", Map.of("quantity", 5)
                )
        );

        // When & Then
        mockMvc.perform(patch("/inventory/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("inventory"))
                .andExpect(jsonPath("$.data.id").value(productId.toString()))
                .andExpect(jsonPath("$.data.attributes.quantity").value(5));
    }

    @Test
    void patchInventory_WithNegativeQuantity_ShouldDecreaseQuantity() throws Exception {
        // Given - First create inventory with 10 items
        Map<String, Object> createRequest = Map.of(
                "data", Map.of(
                        "attributes", Map.of("quantity", 10)
                )
        );

        mockMvc.perform(patch("/inventory/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // When & Then - Decrease by 3
        Map<String, Object> decreaseRequest = Map.of(
                "data", Map.of(
                        "attributes", Map.of("quantity", -3)
                )
        );

        mockMvc.perform(patch("/inventory/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decreaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.quantity").value(7));
    }

    @Test
    void patchInventory_WithZeroQuantity_ShouldReturnSameQuantity() throws Exception {
        // Given - First create inventory
        Map<String, Object> createRequest = Map.of(
                "data", Map.of(
                        "attributes", Map.of("quantity", 10)
                )
        );

        mockMvc.perform(patch("/inventory/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // When & Then - Add 0 quantity
        Map<String, Object> zeroRequest = Map.of(
                "data", Map.of(
                        "attributes", Map.of("quantity", 0)
                )
        );

        mockMvc.perform(patch("/inventory/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zeroRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.quantity").value(10));
    }

    @Test
    void patchInventory_WithLargeQuantity_ShouldHandleCorrectly() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
                "data", Map.of(
                        "attributes", Map.of("quantity", 1000)
                )
        );

        // When & Then
        mockMvc.perform(patch("/inventory/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attributes.quantity").value(1000));
    }
} 