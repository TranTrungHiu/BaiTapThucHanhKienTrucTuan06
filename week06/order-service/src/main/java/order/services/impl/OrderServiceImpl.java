package order.services.impl;

import order.dtos.OrderMessage;
import order.entities.Order;
import order.repositories.OrderRepository;
import order.services.OrderService;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    @Value("${app.rabbit.queue-name}")
    private String queueName;
    @Value("${app.rabbit.exchange-name}")
    private String exchangeName;
    @Value("${app.rabbit.routing-key}")
    private String routingKey;

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Order findById(long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Order save(Order order) {
    	
    	Order savedOrder = orderRepository.save(order);
    	
		OrderMessage message = OrderMessage.builder().id(savedOrder.getId()).productId(savedOrder.getProductId())
				.customerId(savedOrder.getCustomerId()).quantity(savedOrder.getQuantity())
				.status(savedOrder.getStatus()).build();
		
		rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    	
        return orderRepository.save(order);
    }
}
