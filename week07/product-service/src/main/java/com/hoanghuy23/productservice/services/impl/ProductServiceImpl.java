package com.hoanghuy23.productservice.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoanghuy23.productservice.dtos.MessageResponse;
import com.hoanghuy23.productservice.entities.Product;
import com.hoanghuy23.productservice.repositories.ProductRepository;
import com.hoanghuy23.productservice.services.ProductService;
import com.hoanghuy23.productservice.services.event.ProductServiceListener;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService, MessageListener {
    ProductRepository productRepository;
    private static Logger log = LoggerFactory.getLogger(ProductServiceListener.class);

    @Transactional
    @Modifying
    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public void updateStock(Long id, Integer quantity) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    @Override
    public void onMessage(Message message) {
        String body = new String(message.getBody());
        ObjectMapper mapper = new ObjectMapper();
        MessageResponse messageResponse = null;
        try {
            messageResponse = mapper.readValue(body, MessageResponse.class);
            log.info(String.format("Received order: %s", messageResponse));
            updateStock(messageResponse.getProductId(), messageResponse.getQuantity());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
