package com.shop.paymentservice.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderServiceClient {
    @GetMapping("/api/orders/{orderId}")
    OrderResponse getOrder(@PathVariable("orderId") Long orderId);

    @Data
    class OrderResponse {
        private Long id;
        private Long userId;
        private int totalAmount;
        private String status;
    }

}
