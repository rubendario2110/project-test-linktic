package com.testbackend.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testbackend.products.dto.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void contextLoads() {
        assert mockMvc != null;
    }

    @Test
    void createProduct_ShouldReturnJsonApiResponse() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
                "Test Product",
                new BigDecimal("99.99"),
                "Test Description"
        );

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-INTERNAL-API-KEY", "linktic-internal-key-2024")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.type").value("product"))
                .andExpect(jsonPath("$.data.attributes.name").value("Test Product"))
                .andExpect(jsonPath("$.data.attributes.price").value(99.99));
    }

    @Test
    void getProduct_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        ProductRequest request = new ProductRequest(
                "Test Product",
                new BigDecimal("99.99"),
                "Test Description"
        );

        // Create product first
        String createResponse = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-INTERNAL-API-KEY", "linktic-internal-key-2024")
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String productId = objectMapper.readTree(createResponse)
                .get("data").get("id").asText();

        // When & Then
        mockMvc.perform(get("/products/" + productId)
                        .header("X-INTERNAL-API-KEY", "linktic-internal-key-2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("product"))
                .andExpect(jsonPath("$.data.id").value(productId));
    }
} 