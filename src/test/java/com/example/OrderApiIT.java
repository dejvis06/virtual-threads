package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import com.example.orders.api.dto.CreateOrderRequest;
import com.example.orders.api.dto.OrderResponse;
import com.example.orders.domain.Customer;
import com.example.orders.domain.Product;
import com.example.orders.persistence.CustomerRepository;
import com.example.orders.persistence.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@Testcontainers
@AutoConfigureRestTestClient
class OrderApiIT {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("orders")
                    .withUsername("orders")
                    .withPassword("orders");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    RestTestClient client;

    @Autowired
    CustomerRepository customers;
    @Autowired
    ProductRepository products;

    @Test
    void createOrder_thenFetchWithJoin() {
        // seed
        var customer = customers.save(new Customer("Alice", "alice@example.com"));
        var p1 = products.save(new Product("Keyboard", new BigDecimal("49.99")));
        var p2 = products.save(new Product("Mouse", new BigDecimal("19.99")));

        var req = new CreateOrderRequest(
                customer.getId(),
                List.of(
                        new CreateOrderRequest.CreateOrderItem(p1.getId(), 2),
                        new CreateOrderRequest.CreateOrderItem(p2.getId(), 1)
                )
        );

        // POST /api/orders
        OrderResponse created =
                client.post()
                        .uri("/api/orders")
                        .body(req)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(OrderResponse.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals(2, created.items().size());

        // GET /api/orders/{id}
        OrderResponse fetched =
                client.get()
                        .uri("/api/orders/{id}", created.id())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(OrderResponse.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(fetched);
        assertEquals(created.id(), fetched.id());
        assertEquals("Alice", fetched.customer().name());
        assertEquals(2, fetched.items().size());
        assertNotNull(fetched.total());
    }
}
