package com.example.intershop.service;

import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Order findNewOrder() {
        return repository.findByStatus(OrderStatus.NEW)
                .orElseGet(() -> insert(new Order(OrderStatus.NEW)));
    }

    public Order insert(Order order) {
        return repository.save(order);
    }

    public Order update(Order order) {
        return repository.save(order);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
