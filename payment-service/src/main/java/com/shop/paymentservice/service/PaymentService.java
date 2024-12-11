package com.shop.paymentservice.service;

import com.shop.paymentservice.client.OrderServiceClient;
import com.shop.paymentservice.dto.PaymentDto;
import com.shop.paymentservice.entity.Payment;
import com.shop.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;
    private final Random random = new Random();

    @Transactional
    public PaymentDto.PaymentResponse processPayment(PaymentDto.PaymentRequest request) {
        log.info("결제 처리 시도 - orderId: {}, amount: {}", request.getOrderId(), request.getAmount());

        OrderServiceClient.OrderResponse order = orderServiceClient.getOrder(request.getOrderId());

        validateOrderStatus(order);

        if (!order.getUserId().equals(request.getUserId())) {
            log.info("주문 정보가 일치하지 않습니다.");
            throw new RuntimeException("주문자 정보가 일치하지 않습니다.");
        }

        if (order.getTotalAmount() != request.getAmount()) {
            log.info("결제 금액이 일치하지 않습니다.");
            throw new RuntimeException("결제 금액이 일치하지 않습니다.");
        }

        // 결제 정보 생성
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .build();

        try {
            // 모의 결제 처리 (80% 성공, 20% 실패)
            if (random.nextDouble() < 0.8) {
                payment.complete();
                Map<String, String> status = new HashMap<>();
                status.put("status", "PAID");
                orderServiceClient.updateOrderStatus(request.getOrderId(), status);
                log.info("결제 성공 - paymentId: {}, orderId: {}", payment.getOrderId(), payment.getOrderId());
            } else {
                payment.fail();
                Map<String, String> status = new HashMap<>();
                status.put("status", "FAILED");
                orderServiceClient.updateOrderStatus(request.getOrderId(), status);
                log.warn("결제 실패 - paymentId: {}, orderId: {}", payment.getOrderId(), payment.getOrderId());
            }
        } catch (Exception e) {
            payment.fail();
            log.warn("결제 처리 중 오류 발생", e);
            throw new RuntimeException("결제 처리 중 오류 발생", e);
        }


        Payment savedPayment = paymentRepository.save(payment);
        return convertToDto(savedPayment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDto.PaymentResponse> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDto.PaymentResponse> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PaymentDto.PaymentResponse convertToDto(Payment payment) {
        PaymentDto.PaymentResponse response = new PaymentDto.PaymentResponse();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setUserId(payment.getUserId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus().name());
        response.setPaymentDate(payment.getPaymentDate());
        return response;
    }

    private void validateOrderStatus(OrderServiceClient.OrderResponse order) {
        String status = order.getStatus();
        if ("PAID".equals(status)) {
            throw new RuntimeException("이미 결제가 완료된 주문입니다.");
        }
        if ("CANCELLED".equals(status)) {
            throw new RuntimeException("취소된 주문은 결제할 수 없습니다.");
        }
        if (!"CREATED".equals(status)) {
            throw new RuntimeException("결제할 수 없는 주문 상태입니다.");
        }
    }
}
