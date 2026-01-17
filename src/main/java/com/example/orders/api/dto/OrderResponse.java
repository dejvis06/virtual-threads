package com.example.orders.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String status,
        Instant createdAt,
        CustomerSummary customer,
        List<OrderItemResponse> items,
        BigDecimal total
) {
    public record CustomerSummary(Long id, String name, String email) {
    }

    public record OrderItemResponse(Long productId, String productName, int quantity, BigDecimal unitPrice) {
    }
}

