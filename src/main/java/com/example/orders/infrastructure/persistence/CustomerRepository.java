package com.example.orders.infrastructure.persistence;

import com.example.orders.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}