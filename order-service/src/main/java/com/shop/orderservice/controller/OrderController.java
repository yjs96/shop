package com.shop.orderservice.controller;

import com.shop.orderservice.dto.OrderDto;
import com.shop.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto.OrderResponse> createOrder(@RequestBody OrderDto.CreateRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto.OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto.OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}
