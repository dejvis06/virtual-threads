package com.example.orders.infrastructure.persistence;

import com.example.orders.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Fetch-join: Order -> items -> product + customer in one query (good for "get order details")
    @Query("""
                select distinct o from Order o
                join fetch o.customer c
                left join fetch o.items i
                left join fetch i.product p
                where o.id = :id
            """)
    Optional<Order> findByIdWithCustomerAndItems(@Param("id") Long id);
}