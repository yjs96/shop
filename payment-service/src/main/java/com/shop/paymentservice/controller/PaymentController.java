package com.shop.paymentservice.controller;

import com.shop.paymentservice.dto.PaymentDto;
import com.shop.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDto.PaymentResponse> processPayment(@RequestBody PaymentDto.PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDto.PaymentResponse>> getPaymentsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDto.PaymentResponse>> getPaymentsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }
}
