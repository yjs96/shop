package com.shop.orderservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    @Data
    public static class CreateRequest {
        private Long userId;
        private List<OrderItemRequest> items;
    }

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private int quantity;
    }

    @Data
    public static class OrderResponse {
        private Long id;
        private Long userId;
        private int totalAmount;
        private String status;
        private LocalDateTime orderDate;
        private List<OrderItemResponse> items;
    }

    @Data
    public static class OrderItemResponse {
        private Long productId;
        private int quantity;
        private int price;
    }
}
