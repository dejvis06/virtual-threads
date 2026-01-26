package com.example.orders.infrastructure.persistence;

import com.example.orders.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}