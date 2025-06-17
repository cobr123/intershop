package com.example.intershop.repository;

import com.example.intershop.model.Order;
import com.example.intershop.model.OrderStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    Optional<Order> findByStatus(OrderStatus status);
    Iterable<Order> findByStatusIsNot(OrderStatus status);
}
