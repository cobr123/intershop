package com.example.intershop.service;

import com.example.intershop.model.*;
import com.example.intershop.repository.OrderItemRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderItemService {

    private final OrderItemRepository repository;

    public OrderItemService(OrderItemRepository repository) {
        this.repository = repository;
    }

    public Mono<Items> findByTitleLikeOrDescriptionLike(Long orderId, String search, ItemSort itemSort, int pageSize, int pageNumber) {
        return Mono.fromCallable(() -> {
                    var pageable = itemSort.toPageable(pageSize, pageNumber);
                    var searchLike = "%" + search + "%";
                    return Pair.of(pageable, searchLike);
                })
                .flatMap(pair -> repository.findByTitleLikeOrDescriptionLike(orderId, pair.getSecond(), pair.getSecond(), pair.getFirst()).collectList()
                        .flatMap(items -> repository.countByOrderIdAndTitleLikeOrDescriptionLike(orderId, pair.getSecond(), pair.getSecond()).map(count -> Pair.of(items, count)))
                )
                .map(pair -> {
                    var items = pair.getFirst();
                    var totalCount = pair.getSecond();
                    var paging = new Paging(pageNumber, pageSize, totalCount > (long) pageSize * pageNumber, pageNumber > 1);
                    return new Items(ItemUi.grouped(items), paging);
                });
    }

    public Mono<Items> findAll(Long orderId, ItemSort itemSort, int pageSize, int pageNumber) {
        return Mono.just(itemSort.toPageable(pageSize, pageNumber))
                .flatMap(pageable -> repository.findAll(orderId, pageable).collectList())
                .flatMap(items -> repository.countByOrderId(orderId).map(count -> Pair.of(items, count)))
                .map(pair -> {
                    var items = pair.getFirst();
                    var totalCount = pair.getSecond();
                    var paging = new Paging(pageNumber, pageSize, totalCount > (long) pageSize * pageNumber, pageNumber > 1);
                    return new Items(ItemUi.grouped(items), paging);
                });
    }

    public Mono<Void> update(Long orderId, Long itemId, ItemAction action) {
        return repository.findOrderItemByOrderIdAndItemId(orderId, itemId)
                .flatMap(orderItem -> {
                            switch (action) {
                                case PLUS -> {
                                    orderItem.setCount(orderItem.getCount() + 1);
                                    return update(orderItem);
                                }
                                case MINUS -> {
                                    if (orderItem.getCount() > 1) {
                                        orderItem.setCount(orderItem.getCount() - 1);
                                        return update(orderItem);
                                    } else {
                                        return deleteById(orderItem.getId());
                                    }
                                }
                                case DELETE -> {
                                    return deleteById(orderItem.getId());
                                }
                            }
                            return null;
                        }
                ).switchIfEmpty(Mono.fromCallable(() ->
                        switch (action) {
                            case PLUS -> insert(new OrderItem(orderId, itemId, 1));
                            case MINUS, DELETE -> null;
                        }))
                .then();
    }

    public Mono<OrderItem> insert(OrderItem orderItem) {
        return repository.save(orderItem);
    }

    public Mono<OrderItem> findById(Long id) {
        return repository.findById(id);
    }

    public Flux<ItemUi> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId);
    }

    public Mono<ItemUi> findByOrderIdAndItemId(Long orderId, Long itemId) {
        return repository.findByOrderIdAndItemId(orderId, itemId);
    }

    public Mono<OrderItem> update(OrderItem orderItem) {
        return repository.save(orderItem);
    }

    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }
}
