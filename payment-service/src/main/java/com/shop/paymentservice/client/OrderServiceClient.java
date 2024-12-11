package com.shop.paymentservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderServiceClient {
    @GetMapping("/api/orders/{orderId}")
    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderFallback")
    @Retry(name = "orderService")
    @RateLimiter(name = "orderService")
    OrderResponse getOrder(@PathVariable("orderId") Long orderId);

    @Data
    class OrderResponse {
        private Long id;
        private Long userId;
        private int totalAmount;
        private String status;
    }

    default OrderResponse getOrderFallback(Long orderId, Exception ex) {
        throw new RuntimeException(orderId + "주문 정보를 가져올 수 없습니다" + ex);
    }

    @PutMapping("/api/orders/{orderId}/status")
    @CircuitBreaker(name = "orderService", fallbackMethod = "updateOrderStatusFallback")
    void updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody Map<String, String> status);

    default void updateOrderStatusFallback(Long orderId, Map<String, String> status, Exception ex) {
        throw new RuntimeException("주문 상태를 업데이트할 수 없습니다");
    }

}
