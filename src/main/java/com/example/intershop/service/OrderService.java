package com.example.intershop.service;

import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.repository.OrderItemRepository;
import com.example.intershop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Mono<Order> findNewOrder() {
        return orderRepository.findByStatus(OrderStatus.NEW)
                .switchIfEmpty(insert(new Order(OrderStatus.NEW)));
    }

    public Mono<Order> changeNewStatusToGathering() {
        return findNewOrder()
                .flatMap(order -> {
                    order.setStatus(OrderStatus.GATHERING);
                    return update(order);
                });
    }

    public Flux<Order> findAllNotNew() {
        return orderRepository.findByStatusIsNot(OrderStatus.NEW);
    }

    public Mono<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Mono<Order> insert(Order order) {
        return orderRepository.save(order);
    }

    public Mono<Order> update(Order order) {
        return orderRepository.save(order);
    }

    public Mono<Void> deleteById(Long id) {
        return orderItemRepository.deleteById(id)
                .then(orderRepository.deleteById(id));
    }
}
