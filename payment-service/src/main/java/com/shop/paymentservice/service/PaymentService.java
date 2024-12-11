package com.shop.paymentservice.service;

import com.shop.paymentservice.client.OrderServiceClient;
import com.shop.paymentservice.dto.PaymentDto;
import com.shop.paymentservice.entity.Payment;
import com.shop.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;
    private final Random random = new Random();

    @Transactional
    public PaymentDto.PaymentResponse processPayment(PaymentDto.PaymentRequest request) {
        // 주문 정보 확인
        OrderServiceClient.OrderResponse order = orderServiceClient.getOrder(request.getOrderId());

        if (!order.getUserId().equals(request.getUserId())) {
            throw new RuntimeException("주문자 정보가 일치하지 않습니다.");
        }

        if (order.getTotalAmount() != request.getAmount()) {
            throw new RuntimeException("결제 금액이 일치하지 않습니다.");
        }

        // 결제 정보 생성
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .build();

        // 모의 결제 처리 (80% 성공, 20% 실패)
        if (random.nextDouble() < 0.8) {
            payment.complete();
        } else {
            payment.fail();
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
}
