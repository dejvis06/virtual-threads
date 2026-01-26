package com.example.orders.api;

import com.example.orders.api.dto.Price;
import com.example.orders.api.dto.Product;
import com.example.orders.api.dto.ProductPage;
import com.example.orders.api.dto.Stock;
import com.example.orders.application.InventoryService;
import com.example.orders.application.PricingService;
import com.example.orders.application.RecommendationService;
import com.example.orders.infrastructure.cache.ProductCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductCache cache;
    private final PricingService pricing;
    private final InventoryService inventory;
    private final RecommendationService recommendations;

    public ProductController(
            ProductCache cache,
            PricingService pricing,
            InventoryService inventory,
            RecommendationService recommendations
    ) {
        this.cache = cache;
        this.pricing = pricing;
        this.inventory = inventory;
        this.recommendations = recommendations;
    }

    @GetMapping("/{id}")
    public ProductPage view(@PathVariable long id) {
        Product product = cache.get(id);              // ~1 ms CPU

        Price price = pricing.getPrice(id);           // ~1 s wait
        Stock stock = inventory.getStock(id);         // ~1 s wait
        List<Product> recs = recommendations.get(id); // ~1 s wait

        return new ProductPage(product, price, stock, recs);
    }
}
