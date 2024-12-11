package com.shop.orderservice.controller;

import com.shop.orderservice.dto.OrderDto;
import com.shop.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto.OrderResponse> createOrder(@RequestBody OrderDto.CreateRequest request) {
        log.info("주문 생성 요청: userId={}, itemCount={}",
                request.getUserId(), request.getItems().size());
        try {
            OrderDto.OrderResponse response = orderService.createOrder(request);
            log.info("주문 생성 성공: orderId={}, totalAmount={}",
                    response.getId(), response.getTotalAmount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("주문 생성 실패: userId={}", request.getUserId(), e);
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto.OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto.OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto.OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request.get("status")));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto.OrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }
}
