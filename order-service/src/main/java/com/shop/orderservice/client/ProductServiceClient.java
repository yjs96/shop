package com.shop.orderservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductServiceClient {
    @GetMapping("/api/products/{productId}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    @Retry(name = "productService")
    @RateLimiter(name = "productService")
    ProductResponse getProduct(@PathVariable("productId") Long productId);

    @Data
    class ProductResponse {
        private Long id;
        private String name;
        private int price;
        private int stock;
    }

    default ProductResponse getProductFallback(Long productId, Exception ex) {
        throw new RuntimeException(productId + "상품 정보를 가져올 수 없습니다" + ex);
    }
}
