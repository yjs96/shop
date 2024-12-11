package com.shop.paymentservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class PaymentDto {
    @Data
    public static class PaymentRequest {
        private Long orderId;
        private Long userId;
        private int amount;
    }

    @Data
    public static class PaymentResponse {
        private Long id;
        private Long orderId;
        private Long userId;
        private int amount;
        private String status;
        private LocalDateTime paymentDate;
    }
}
