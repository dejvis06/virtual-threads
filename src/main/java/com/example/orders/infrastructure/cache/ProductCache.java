package com.example.orders.infrastructure.cache;

import com.example.orders.api.dto.Product;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProductCache {

    private final Map<Long, Product> cache = new ConcurrentHashMap<>();

    public ProductCache() {
        cache.put(1L, new Product(1L, "Keyboard"));
    }

    public Product get(long id) {
        return cache.get(id);
    }
}
