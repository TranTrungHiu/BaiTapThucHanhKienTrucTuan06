package com.trunghieu21132291.orderservice.services;

import com.trunghieu21132291.orderservice.entities.Order;

public interface OrderService {
    Order save(Order order);

    Order findById(long id);
}
