package com.testbackend.products;

import com.testbackend.products.domain.Product;
import com.testbackend.products.dto.ProductRequest;
import com.testbackend.products.dto.ProductResponse;
import com.testbackend.products.repository.ProductRepository;
import com.testbackend.products.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductRequest testProductRequest;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .description("Test Description")
                .build();

        testProductRequest = new ProductRequest(
                "Test Product",
                new BigDecimal("99.99"),
                "Test Description"
        );
    }

    @Test
    void create_ShouldReturnProductResponse() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductResponse result = productService.create(testProductRequest);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getName(), result.name());
        assertEquals(testProduct.getPrice(), result.price());
        assertEquals(testProduct.getDescription(), result.description());
    }

    @Test
    void get_WhenProductExists_ShouldReturnProduct() {
        // Given
        UUID productId = testProduct.getId();
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        ProductResponse result = productService.get(productId);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.id());
        assertEquals(testProduct.getName(), result.name());
    }

    @Test
    void get_WhenProductNotExists_ShouldThrowException() {
        // Given
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> productService.get(productId));
    }
} 