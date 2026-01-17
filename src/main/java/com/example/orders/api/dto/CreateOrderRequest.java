package com.example.orders.api.dto;

import java.util.List;

public record CreateOrderRequest(
        Long customerId,
        List<CreateOrderItem> items
) {
    public record CreateOrderItem(Long productId, int quantity) {
    }
}

