package com.example.intershop.service;

import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.repository.OrderItemRepository;
import com.example.intershop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order findNewOrder() {
        return orderRepository.findByStatus(OrderStatus.NEW)
                .orElseGet(() -> insert(new Order(OrderStatus.NEW)));
    }

    public Iterable<Order> findAllNotNew() {
        return orderRepository.findByStatusIsNot(OrderStatus.NEW);
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Order insert(Order order) {
        return orderRepository.save(order);
    }

    public Order update(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteById(Long id) {
        orderItemRepository.deleteById(id);
        orderRepository.deleteById(id);
    }
}
