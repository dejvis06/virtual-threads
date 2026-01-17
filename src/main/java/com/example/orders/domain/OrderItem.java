package com.example.orders.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    static OrderItem of(Order order, Product product, int quantity, BigDecimal unitPrice) {
        OrderItem oi = new OrderItem();
        oi.order = order;
        oi.product = product;
        oi.quantity = quantity;
        oi.unitPrice = unitPrice;
        return oi;
    }
}