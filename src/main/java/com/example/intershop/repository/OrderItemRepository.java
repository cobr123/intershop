package com.example.intershop.repository;

import com.example.intershop.model.ItemUi;
import com.example.intershop.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long>, PagingAndSortingRepository<OrderItem, Long> {
    Optional<OrderItem> findOrderItemByOrderIdAndItemId(Long orderId, Long itemId);

    @Query(value = "select i.id, i.title, i.description, i.img_path, oi.count, i.price from items i left join order_items oi on oi.order_id = ?1 and i.id = oi.item_id where i.id = ?2", nativeQuery = true)
    Optional<ItemUi> findByOrderIdAndItemId(Long orderId, Long itemId);

    @Query(value = "select i.id, i.title, i.description, i.img_path, oi.count, i.price from items i, order_items oi where oi.order_id = ?1 and i.id = oi.item_id", nativeQuery = true)
    List<ItemUi> findByOrderId(Long orderId);

    @Query(value = "select i.id, i.title, i.description, i.img_path, oi.count, i.price from items i left join order_items oi on oi.item_id = i.id and oi.order_id = ?1", nativeQuery = true)
    Page<ItemUi> findByTitleLikeOrDescriptionLike(Long orderId, String title, String description, Pageable pageable);

    @Query(value = "select i.id, i.title, i.description, i.img_path, oi.count, i.price from items i left join order_items oi on oi.item_id = i.id and oi.order_id = ?1", nativeQuery = true)
    Page<ItemUi> findAll(Long orderId, Pageable pageable);
}