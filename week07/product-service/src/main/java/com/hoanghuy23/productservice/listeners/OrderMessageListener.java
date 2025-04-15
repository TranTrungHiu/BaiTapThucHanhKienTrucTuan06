package com.hoanghuy23.productservice.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghuy23.productservice.dtos.MessageResponse;
import com.hoanghuy23.productservice.services.ProductService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageListener {
    private final ProductService productService;
    public OrderMessageListener(ProductService productService) {
        this.productService = productService;
    }
    @RabbitListener(queues = "${app.rabbit.queue-name}")
    public void receiveOrder(Message message) {
       String body = new String(message.getBody());
       System.out.println(body);
        ObjectMapper mapper = new ObjectMapper();
        MessageResponse messageResponse = null;
        try {
            messageResponse = mapper.readValue(body, MessageResponse.class);
            System.out.println(String.format("Received order: %s", messageResponse));
            productService.updateStock(messageResponse.getProductId(), messageResponse.getQuantity());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
