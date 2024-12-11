package com.shop.orderservice.service;

import com.shop.orderservice.client.ProductServiceClient;
import com.shop.orderservice.dto.OrderDto;
import com.shop.orderservice.entity.Order;
import com.shop.orderservice.entity.OrderItem;
import com.shop.orderservice.entity.OrderStatus;
import com.shop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Transactional
    public OrderDto.OrderResponse createOrder(OrderDto.CreateRequest request) {
        log.info("주문 생성 시도 - userId: {}", request.getUserId());
        int totalAmount = 0;
        Order order = Order.builder()
                .userId(request.getUserId())
                .totalAmount(0)
                .build();

        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> {
                    ProductServiceClient.ProductResponse product =
                            productServiceClient.getProduct(item.getProductId());

                    return OrderItem.builder()
                            .order(order)
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .price(product.getPrice())
                            .build();
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.setTotalAmount(orderItems.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum());

        Order savedOrder = orderRepository.save(order);
        log.info("주문 생성 성공 - orderId: {}, totalAmount: {}",
                savedOrder.getId(), savedOrder.getTotalAmount());
        return convertToDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderDto.OrderResponse> getOrdersByUserId(Long userId) {
        log.info("사용자별 주문 목록 조회 - userId: {}", userId);

        return orderRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto.OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDto(order);
    }

    private OrderDto.OrderResponse convertToDto(Order order) {
        OrderDto.OrderResponse response = new OrderDto.OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus().name());
        response.setOrderDate(order.getOrderDate());

        List<OrderDto.OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> {
                    OrderDto.OrderItemResponse itemResponse = new OrderDto.OrderItemResponse();
                    itemResponse.setProductId(item.getProductId());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setPrice(item.getPrice());
                    return itemResponse;
                })
                .collect(Collectors.toList());

        response.setItems(items);
        return response;
    }

    @Transactional
    public OrderDto.OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 주문입니다."));

        try {
            order.setStatus(OrderStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("잘못된 주문 상태입니다.");
        }

        return convertToDto(order);
    }

    @Transactional
    public OrderDto.OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 주문입니다."));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("취소할 수 없는 주문 상태입니다");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return convertToDto(order);
    }
}
