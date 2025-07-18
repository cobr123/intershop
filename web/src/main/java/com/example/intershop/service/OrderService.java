package com.example.intershop.service;

import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import com.example.intershop.repository.OrderItemRepository;
import com.example.intershop.repository.OrderRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "orders", key = "'new'")
    public Mono<Order> findNewOrder() {
        return orderRepository.findByStatus(OrderStatus.NEW)
                .switchIfEmpty(insert(new Order(OrderStatus.NEW)));
    }

    @CacheEvict(value = "orders", key = "'new'")
    public Mono<Order> changeNewStatusToGathering(Order order) {
        order.setStatus(OrderStatus.GATHERING);
        return update(order);
    }

    public Flux<Order> findAllNotNew() {
        return orderRepository.findByStatusIsNot(OrderStatus.NEW);
    }

    @Cacheable(value = "orders", key = "#id")
    public Mono<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @CachePut(value = "orders", key = "#result.id")
    public Mono<Order> insert(Order order) {
        return orderRepository.save(order);
    }

    @CachePut(value = "orders", key = "#order.id")
    public Mono<Order> update(Order order) {
        return orderRepository.save(order);
    }

    @CacheEvict(value = "orders", key = "#id")
    public Mono<Void> deleteById(Long id) {
        return orderItemRepository.deleteByOrderId(id)
                .then(orderRepository.deleteById(id));
    }
}
