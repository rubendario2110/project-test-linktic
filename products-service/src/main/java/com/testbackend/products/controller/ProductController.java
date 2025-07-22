package com.testbackend.products.controller;

import com.testbackend.products.dto.ProductRequest;
import com.testbackend.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@Valid @RequestBody ProductRequest req) {
        var pr = service.create(req);
        return Map.of("data", Map.of("type", "product", "id", pr.id(), "attributes", pr));
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable UUID id) {
        var pr = service.get(id);
        return Map.of("data", Map.of("type", "product", "id", pr.id(), "attributes", pr));
    }

    @GetMapping
    public Map<String, Object> list(@PageableDefault(size = 20) Pageable pageable) {
        var page = service.list(pageable);
        return Map.of("data", page.getContent().stream()
                .map(pr -> Map.of("type", "product", "id", pr.id(), "attributes", pr))
                .toList());
    }
}
