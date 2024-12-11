package com.shop.orderservice.service;

import com.shop.orderservice.client.ProductServiceClient;
import com.shop.orderservice.dto.OrderDto;
import com.shop.orderservice.entity.Order;
import com.shop.orderservice.entity.OrderItem;
import com.shop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Transactional
    public OrderDto.OrderResponse createOrder(OrderDto.CreateRequest request) {
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
        return convertToDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderDto.OrderResponse> getOrdersByUserId(Long userId) {
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
}
