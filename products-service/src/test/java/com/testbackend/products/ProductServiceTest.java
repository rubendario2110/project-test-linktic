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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private UUID productId;
    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .description("Test Description")
                .createdAt(Instant.now())
                .build();
        
        productRequest = new ProductRequest("Test Product", new BigDecimal("99.99"), "Test Description");
        productResponse = ProductResponse.from(product);
    }

    @Test
    void create_WhenValidRequest_ShouldReturnProductResponse() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When
        ProductResponse result = productService.create(productRequest);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.id());
        assertEquals("Test Product", result.name());
        assertEquals(new BigDecimal("99.99"), result.price());
        assertEquals("Test Description", result.description());
    }

    @Test
    void create_WithEmptyName_ShouldReturnProductResponse() {
        // Given
        ProductRequest emptyNameRequest = new ProductRequest("", new BigDecimal("50.00"), "Description");
        Product productWithEmptyName = Product.builder()
                .id(productId)
                .name("")
                .price(new BigDecimal("50.00"))
                .description("Description")
                .createdAt(Instant.now())
                .build();
        
        when(productRepository.save(any(Product.class))).thenReturn(productWithEmptyName);

        // When
        ProductResponse result = productService.create(emptyNameRequest);

        // Then
        assertNotNull(result);
        assertEquals("", result.name());
    }

    @Test
    void create_WithZeroPrice_ShouldReturnProductResponse() {
        // Given
        ProductRequest zeroPriceRequest = new ProductRequest("Free Product", BigDecimal.ZERO, "Free description");
        Product freeProduct = Product.builder()
                .id(productId)
                .name("Free Product")
                .price(BigDecimal.ZERO)
                .description("Free description")
                .createdAt(Instant.now())
                .build();
        
        when(productRepository.save(any(Product.class))).thenReturn(freeProduct);

        // When
        ProductResponse result = productService.create(zeroPriceRequest);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.price());
    }

    @Test
    void get_WhenProductExists_ShouldReturnProductResponse() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When
        ProductResponse result = productService.get(productId);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.id());
        assertEquals("Test Product", result.name());
        assertEquals(new BigDecimal("99.99"), result.price());
    }

    @Test
    void get_WhenProductDoesNotExist_ShouldThrowException() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.get(productId);
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("PRODUCT_NOT_FOUND", exception.getReason());
    }

    @Test
    void list_WhenProductsExist_ShouldReturnPageOfProducts() {
        // Given
        Product product2 = Product.builder()
                .id(UUID.randomUUID())
                .name("Product 2")
                .price(new BigDecimal("50.00"))
                .description("Description 2")
                .createdAt(Instant.now())
                .build();
        
        List<Product> products = List.of(product, product2);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 20), 2);
        
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.list(PageRequest.of(0, 20));

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Test Product", result.getContent().get(0).name());
        assertEquals("Product 2", result.getContent().get(1).name());
    }

    @Test
    void list_WhenNoProducts_ShouldReturnEmptyPage() {
        // Given
        Page<Product> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // When
        Page<ProductResponse> result = productService.list(PageRequest.of(0, 20));

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void list_SimpleMockTest() {
        // Given - Simple test to verify mock is working
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // When
        Page<ProductResponse> result = productService.list(pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        
        // Verify that the mock was called
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    void list_MockDiagnosticTest() {
        // Given - Diagnostic test to see what's happening
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> testPage = new PageImpl<>(List.of(product), pageRequest, 1);
        
        System.out.println("Setting up mock to return page with 1 element");
        when(productRepository.findAll(any(Pageable.class))).thenReturn(testPage);

        // When
        System.out.println("Calling service.list()");
        Page<ProductResponse> result = productService.list(pageRequest);

        // Then
        System.out.println("Result total elements: " + result.getTotalElements());
        System.out.println("Result content size: " + result.getContent().size());
        
        // This should pass if mock is working
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        
        // Verify that the mock was called
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    void list_ExactMockTest() {
        // Given - Test with exact PageRequest to avoid any matcher issues
        PageRequest pageRequest = PageRequest.of(1, 5);
        Page<Product> testPage = new PageImpl<>(List.of(product), pageRequest, 1);
        
        System.out.println("Setting up exact mock for PageRequest.of(1, 5)");
        when(productRepository.findAll(pageRequest)).thenReturn(testPage);

        // When
        System.out.println("Calling service.list() with exact PageRequest");
        Page<ProductResponse> result = productService.list(pageRequest);

        // Then
        System.out.println("Result total elements: " + result.getTotalElements());
        System.out.println("Result content size: " + result.getContent().size());
        
        // This should pass if mock is working
        assertEquals(6, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        
        // Verify that the mock was called with exact parameters
        verify(productRepository).findAll(pageRequest);
    }

    @Test
    void list_VerySimpleTest() {
        // Given - Very simple test to verify mock is working
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);
        
        System.out.println("Setting up very simple mock");
        when(productRepository.findAll(pageRequest)).thenReturn(emptyPage);

        // When
        System.out.println("Calling service.list()");
        Page<ProductResponse> result = productService.list(pageRequest);

        // Then
        System.out.println("Result total elements: " + result.getTotalElements());
        System.out.println("Result content size: " + result.getContent().size());
        
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        
        // Verify that the mock was called
        verify(productRepository).findAll(pageRequest);
    }

    @Test
    void create_WithLargePrice_ShouldHandleCorrectly() {
        // Given
        ProductRequest largePriceRequest = new ProductRequest("Expensive Product", new BigDecimal("999999.99"), "Very expensive");
        Product expensiveProduct = Product.builder()
                .id(productId)
                .name("Expensive Product")
                .price(new BigDecimal("999999.99"))
                .description("Very expensive")
                .createdAt(Instant.now())
                .build();
        
        when(productRepository.save(any(Product.class))).thenReturn(expensiveProduct);

        // When
        ProductResponse result = productService.create(largePriceRequest);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("999999.99"), result.price());
    }
} 