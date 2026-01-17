package com.example.orders;

import com.example.orders.api.dto.CreateOrderRequest;
import com.example.orders.api.dto.OrderResponse;
import com.example.orders.domain.*;
import com.example.orders.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final CustomerRepository customers;
    private final ProductRepository products;

    public OrderService(OrderRepository orders, CustomerRepository customers, ProductRepository products) {
        this.orders = orders;
        this.customers = customers;
        this.products = products;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest req) {
        Customer customer = customers.findById(req.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + req.customerId()));

        Order order = Order.create(customer);

        for (var item : req.items()) {
            Product product = products.findById(item.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.productId()));

            if (item.quantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be > 0 for productId=" + item.productId());
            }

            order.addItem(product, item.quantity(), product.getPrice());
        }

        Order saved = orders.save(order);
        // saved has items persisted because of CascadeType.ALL
        return toResponse(saved);
    }

    @Transactional
    public OrderResponse getById(Long orderId) {
        Order order = orders.findByIdWithCustomerAndItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        return toResponse(order);
    }

    private static OrderResponse toResponse(Order o) {
        var customer = o.getCustomer();
        return new OrderResponse(
                o.getId(),
                o.getStatus().name(),
                o.getCreatedAt(),
                new OrderResponse.CustomerSummary(customer.getId(), customer.getName(), customer.getEmail()),
                o.getItems().stream()
                        .map(i -> new OrderResponse.OrderItemResponse(
                                i.getProduct().getId(),
                                i.getProduct().getName(),
                                i.getQuantity(),
                                i.getUnitPrice()
                        ))
                        .collect(Collectors.toList()),
                o.total()
        );
    }
}
