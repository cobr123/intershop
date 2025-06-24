package com.example.intershop.repository;

import com.example.intershop.model.ItemUi;
import com.example.intershop.model.OrderItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {
    Mono<OrderItem> findOrderItemByOrderIdAndItemId(Long orderId, Long itemId);

    @Query("select i.id, i.title, i.description, i.img_path, coalesce(oi.count, 0) as count, i.price from items i left join order_items oi on oi.order_id = :orderId and i.id = oi.item_id where i.id = :itemId")
    Mono<ItemUi> findByOrderIdAndItemId(Long orderId, Long itemId);

    @Query("select i.id, i.title, i.description, i.img_path, oi.count, i.price from items i, order_items oi where oi.order_id = :orderId and i.id = oi.item_id")
    Flux<ItemUi> findByOrderId(Long orderId);

    @Query("select i.id, i.title, i.description, i.img_path, coalesce(oi.count, 0) as count, i.price from items i left join order_items oi on oi.item_id = i.id and oi.order_id = :orderId where (i.title like :title or i.description like :description)")
    Flux<ItemUi> findByTitleLikeOrDescriptionLike(Long orderId, String title, String description, Sort sort);

    @Query("select i.id, i.title, i.description, i.img_path, coalesce(oi.count, 0) as count, i.price from items i left join order_items oi on oi.item_id = i.id and oi.order_id = :orderId")
    Flux<ItemUi> findAll(Long orderId, Sort sort);

    @Query("select count(*) from items i left join order_items oi on oi.item_id = i.id and oi.order_id = :orderId where (i.title like :title or i.description like :description)")
    Mono<Long> countByOrderIdAndTitleLikeOrDescriptionLike(Long orderId, String title, String description);
}