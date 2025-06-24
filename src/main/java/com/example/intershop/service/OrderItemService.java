package com.example.intershop.service;

import com.example.intershop.model.*;
import com.example.intershop.repository.ItemRepository;
import com.example.intershop.repository.OrderItemRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, ItemRepository itemRepository) {
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
    }

    public Mono<Items> findByTitleLikeOrDescriptionLike(Long orderId, String search, ItemSort itemSort, int pageSize, int pageNumber) {
        return Mono.fromCallable(() -> {
                    var sort = itemSort.toSort();
                    var searchLike = "%" + search + "%";
                    return Pair.of(sort, searchLike);
                })
                .flatMap(pair -> withOffset(orderItemRepository.findByTitleLikeOrDescriptionLike(orderId, pair.getSecond(), pair.getSecond(), pair.getFirst()), pageSize, pageNumber)
                        .map(items -> Pair.of(items, pair.getSecond())))
                .flatMap(pair -> orderItemRepository.countByOrderIdAndTitleLikeOrDescriptionLike(orderId, pair.getSecond(), pair.getSecond())
                        .map(count -> Pair.of(pair.getFirst(), count)))
                .map(pair -> {
                    var items = pair.getFirst();
                    var totalCount = pair.getSecond();
                    var paging = new Paging(pageNumber, pageSize, totalCount > (long) pageSize * pageNumber, pageNumber > 1);
                    return new Items(ItemUi.grouped(items), paging);
                });
    }

    public Mono<Items> findAll(Long orderId, ItemSort itemSort, int pageSize, int pageNumber) {
        return Mono.just(itemSort.toSort())
                .flatMap(sort -> withOffset(orderItemRepository.findAll(orderId, sort), pageSize, pageNumber))
                .flatMap(items -> itemRepository.count().map(count -> Pair.of(items, count)))
                .map(pair -> {
                    var items = pair.getFirst();
                    var totalCount = pair.getSecond();
                    var paging = new Paging(pageNumber, pageSize, totalCount > (long) pageSize * pageNumber, pageNumber > 1);
                    return new Items(ItemUi.grouped(items), paging);
                });
    }

    private Mono<List<ItemUi>> withOffset(Flux<ItemUi> flux, int pageSize, int pageNumber) {
        var offset = getOffset(pageSize, pageNumber);
        if (offset > 0) {
            return flux.skip(offset).take(pageSize).collectList();
        } else {
            return flux.take(pageSize).collectList();
        }
    }

    private int getOffset(int pageSize, int pageNumber) {
        return (pageNumber - 1) * pageSize;
    }

    public Mono<Void> update(Long orderId, Long itemId, ItemAction action) {
        return orderItemRepository.findOrderItemByOrderIdAndItemId(orderId, itemId)
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
        return orderItemRepository.save(orderItem);
    }

    public Mono<OrderItem> findById(Long id) {
        return orderItemRepository.findById(id);
    }

    public Flux<ItemUi> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public Mono<ItemUi> findByOrderIdAndItemId(Long orderId, Long itemId) {
        return orderItemRepository.findByOrderIdAndItemId(orderId, itemId);
    }

    public Mono<OrderItem> update(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public Mono<Void> deleteById(Long id) {
        return orderItemRepository.deleteById(id);
    }

    public Mono<Boolean> existsById(Long id) {
        return orderItemRepository.existsById(id);
    }
}
