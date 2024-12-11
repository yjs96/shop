package com.shop.orderservice.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductServiceClient {
    @GetMapping("/api/products/{productId}")
    ProductResponse getProduct(@PathVariable("productId") Long productId);

    @Data
    class ProductResponse {
        private Long id;
        private String name;
        private int price;
        private int stock;
    }

}
