package com.example.intershop.repository;

import com.example.intershop.model.Item;
import com.example.intershop.model.OrderItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
    Optional<OrderItem> findByOrderIdAndItemId(Long orderId, Long itemId);

    @Query(value = "select items.* from items, order_items where items.order_id = :orderId and items.id = order_items.item_id")
    List<Item> findByOrderId(@Param("orderId") Long orderId);
}
