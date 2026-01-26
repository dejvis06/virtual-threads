package com.example.orders.api.dto;

import java.util.List;

public record ProductPage(
        Product product,
        Price price,
        Stock stock,
        List<Product> recommendations
) {
}