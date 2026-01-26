package com.example.orders.application;

import com.example.orders.api.dto.Stock;
import com.example.orders.infrastructure.HttpStatUtil;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    public Stock getStock(long productId) {
        HttpStatUtil.okWithServerDelay(1); // simulate HTTP latency
        return new Stock(42);
    }
}
