package com.hoanghuy23.productservice.services.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghuy23.productservice.dtos.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceListener implements MessageListener {
    private static Logger log = LoggerFactory.getLogger(ProductServiceListener.class);
    @Override
    public void onMessage(Message message) {
        String body = new String(message.getBody());
        ObjectMapper mapper = new ObjectMapper();
        MessageResponse messageResponse = null;
        try {
            messageResponse = mapper.readValue(body, MessageResponse.class);
            log.info(String.format("Received order: %s", messageResponse));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void containerAckMode(AcknowledgeMode mode) {
        MessageListener.super.containerAckMode(mode);
    }

    @Override
    public boolean isAsyncReplies() {
        return MessageListener.super.isAsyncReplies();
    }

    @Override
    public void onMessageBatch(List<Message> messages) {
        MessageListener.super.onMessageBatch(messages);
    }
}
