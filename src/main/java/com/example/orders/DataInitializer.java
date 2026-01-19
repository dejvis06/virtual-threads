package com.example.orders;

import com.example.orders.domain.Customer;
import com.example.orders.domain.Product;
import com.example.orders.persistence.CustomerRepository;
import com.example.orders.persistence.ProductRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
@Transactional
public class DataInitializer {

    private final CustomerRepository customers;
    private final ProductRepository products;

    // expose these for logging / later use if needed
    public static Long customerId;
    public static Long product1Id;
    public static Long product2Id;

    public DataInitializer(CustomerRepository customers,
                           ProductRepository products) {
        this.customers = customers;
        this.products = products;
    }

    @PostConstruct
    void init() {
        Customer c;
        Product p1;
        Product p2;

        if (customers.count() == 0) {
            c = customers.save(new Customer("LoadUser", "load@test.com"));
        } else {
            c = customers.findAll().getFirst();
        }

        if (products.count() < 2) {
            products.deleteAll(); // optional: ensure deterministic IDs
            p1 = products.save(new Product("Item-1", new BigDecimal("10.00")));
            p2 = products.save(new Product("Item-2", new BigDecimal("20.00")));
        } else {
            var list = products.findAll();
            p1 = list.get(0);
            p2 = list.get(1);
        }

        customerId = c.getId();
        product1Id = p1.getId();
        product2Id = p2.getId();

        System.out.println("Load data ready:");
        System.out.println("customerId=" + customerId);
        System.out.println("product1Id=" + product1Id);
        System.out.println("product2Id=" + product2Id);
    }
}

