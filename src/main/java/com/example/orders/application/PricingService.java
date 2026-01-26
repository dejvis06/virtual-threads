package com.example.orders.application;

import com.example.orders.api.dto.Price;
import com.example.orders.infrastructure.HttpStatUtil;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    public Price getPrice(long productId) {
        HttpStatUtil.okWithServerDelay(1); // simulate HTTP latency
        return new Price(99.99);
    }
}
