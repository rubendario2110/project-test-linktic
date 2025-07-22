package com.testbackend.inventory.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "productClient", url = "${PRODUCTS_SERVICE_BASE_URL}")
public interface ProductClient {
    @GetMapping("/products/{id}")
    Map<String, Object> get(@PathVariable("id") UUID id);
}
