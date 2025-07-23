package com.testbackend.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testbackend.products.dto.ProductRequest;
import com.testbackend.products.dto.ProductResponse;
import com.testbackend.products.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ProductService productService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID productId;
    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
        productId = UUID.randomUUID();
        productRequest = new ProductRequest("Test Product", new BigDecimal("99.99"), "Test Description");
        productResponse = new ProductResponse(productId, "Test Product", new BigDecimal("99.99"), "Test Description", java.time.Instant.now());
    }

    @Test
    void create_WhenValidRequest_ShouldReturnCreatedProduct() throws Exception {
        // Given
        when(productService.create(any(ProductRequest.class))).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.type").value("product"))
                .andExpect(jsonPath("$.data.id").value(productId.toString()))
                .andExpect(jsonPath("$.data.attributes.name").value("Test Product"))
                .andExpect(jsonPath("$.data.attributes.price").value(99.99))
                .andExpect(jsonPath("$.data.attributes.description").value("Test Description"));
    }

    @Test
    void create_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        ProductRequest invalidRequest = new ProductRequest("", new BigDecimal("-1"), "");

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        when(productService.get(productId)).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("product"))
                .andExpect(jsonPath("$.data.id").value(productId.toString()))
                .andExpect(jsonPath("$.data.attributes.name").value("Test Product"));
    }

    @Test
    void get_WhenProductNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(productService.get(productId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND"));

        // When & Then
        mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void list_WhenProductsExist_ShouldReturnProductsList() throws Exception {
        // Given
        ProductResponse product2 = new ProductResponse(UUID.randomUUID(), "Product 2", new BigDecimal("50.00"), "Description 2", java.time.Instant.now());
        List<ProductResponse> products = List.of(productResponse, product2);
        Page<ProductResponse> page = new PageImpl<>(products, PageRequest.of(0, 20), 2);
        
        when(productService.list(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].type").value("product"))
                .andExpect(jsonPath("$.data[0].id").value(productId.toString()))
                .andExpect(jsonPath("$.data[1].type").value("product"));
    }

    @Test
    void list_WhenNoProducts_ShouldReturnEmptyList() throws Exception {
        // Given
        Page<ProductResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(productService.list(any(Pageable.class))).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void list_WithPagination_ShouldReturnPaginatedResults() throws Exception {
        // Given
        Page<ProductResponse> page = new PageImpl<>(List.of(productResponse), PageRequest.of(1, 5), 1);
        when(productService.list(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/products?page=1&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));
    }
} 