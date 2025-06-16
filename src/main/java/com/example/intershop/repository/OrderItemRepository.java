package com.example.intershop.repository;

import com.example.intershop.model.OrderItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
    Optional<OrderItem> findByOrderIdAndItemId(Long orderId, Long itemId);
}
