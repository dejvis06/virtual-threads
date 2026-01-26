package com.example.orders.application;

import com.example.orders.api.dto.Product;
import com.example.orders.infrastructure.HttpStatUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    public List<Product> get(long productId) {
        HttpStatUtil.okWithServerDelay(1); // simulate HTTP latency
        return List.of(
                new Product(2L, "Mouse"),
                new Product(3L, "Headset")
        );
    }
}

