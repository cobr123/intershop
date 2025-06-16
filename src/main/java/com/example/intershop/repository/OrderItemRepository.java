package com.example.intershop.repository;

import com.example.intershop.model.Item;
import com.example.intershop.model.OrderItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
    Optional<OrderItem> findByOrderIdAndItemId(Long orderId, Long itemId);

    @Query(value = "select i.* from items i, order_items oi where oi.order_id = ?1 and i.id = oi.item_id", nativeQuery = true)
    List<Item> findByOrderId(Long orderId);
}