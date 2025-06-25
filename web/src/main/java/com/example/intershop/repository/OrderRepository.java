package com.example.intershop.repository;

import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    Mono<Order> findByStatus(OrderStatus status);
    Flux<Order> findByStatusIsNot(OrderStatus status);
}
