package com.hoanghuy23.orderservice.services.impl;

import com.hoanghuy23.orderservice.dtos.OrderMessage;
import com.hoanghuy23.orderservice.entities.Order;
import com.hoanghuy23.orderservice.repositories.OrderRepository;
import com.hoanghuy23.orderservice.services.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderServiceImpl implements OrderService {
    @Value("${app.rabbit.queue-name}")
      String queueName;
    @Value("${app.rabbit.exchange-name}")
     String exchangeName;
    @Value("${app.rabbit.routing-key}")
     String routingKey;

    final OrderRepository orderRepository;
    final RabbitTemplate rabbitTemplate;

    @Override
    public Order save(Order order) {
        var savedOrder = orderRepository.save(order);
        var orderMessage = OrderMessage.builder()
                .orderId(order.getId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .build();
        rabbitTemplate.convertAndSend(exchangeName, routingKey, orderMessage);
        return savedOrder;
    }

    @Override
    public Order findById(long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
