package product.listeners;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import product.dtos.MessageResponse;

public class OrderMessageListener {
	@RabbitListener(queues = "${app.rabbit.queue-name}")
    public void receiveOrder(Message message) {
        String body = new String(message.getBody());
        System.out.println(body);
        ObjectMapper mapper = new ObjectMapper();
        MessageResponse messageResponse = null;

        try {
            messageResponse = mapper.readValue(body, MessageResponse.class);

            System.out.println(String.format(
                    "Notifying user with id = %d for order status change to %s with order id = %d",
                    messageResponse.getCustomerId(),
                    messageResponse.getStatus(),
                    messageResponse.getProductId(),
                    messageResponse.getQuantity()
            ));

        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
    }
}
