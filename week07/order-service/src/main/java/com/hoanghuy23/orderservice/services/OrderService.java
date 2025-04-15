package com.hoanghuy23.orderservice.services;

import com.hoanghuy23.orderservice.entities.Order;

public interface OrderService {
    Order save(Order order);
    Order findById(long id);
}
