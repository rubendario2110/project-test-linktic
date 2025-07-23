package com.testbackend.products.service;

import com.testbackend.products.domain.Product;
import com.testbackend.products.dto.ProductRequest;
import com.testbackend.products.dto.ProductResponse;
import com.testbackend.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repo;

    public ProductResponse create(ProductRequest req) {
        Product p = Product.builder()
                .name(req.name())
                .price(req.price())
                .description(req.description())
                .build();
        return ProductResponse.from(repo.save(p));
    }

    public ProductResponse get(UUID id) {
        return repo.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND"));
    }

    public Page<ProductResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(ProductResponse::from);
    }
}
