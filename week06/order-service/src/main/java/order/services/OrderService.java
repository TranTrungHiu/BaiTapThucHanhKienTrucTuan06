package order.services;


import order.entities.Order;

public interface OrderService {
    public Order save(Order order);
    public Order findById(long id);
}
