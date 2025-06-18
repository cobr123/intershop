package com.example.intershop.service;

import com.example.intershop.model.*;
import com.example.intershop.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    private final OrderItemRepository repository;

    public OrderItemService(OrderItemRepository repository) {
        this.repository = repository;
    }

    public Items findByTitleLikeOrDescriptionLike(Long orderId, String search, ItemSort itemSort, int pageSize, int pageNumber) {
        var pageable = itemSort.toPageable(pageSize, pageNumber);
        var searchLike = "%" + search + "%";
        var page = repository.findByTitleLikeOrDescriptionLike(orderId, searchLike, searchLike, pageable);
        var paging = new Paging(pageNumber, pageSize, page.hasNext(), page.hasPrevious());
        return new Items(ItemUi.grouped(page.stream().toList()), paging);
    }

    public Items findAll(Long orderId, ItemSort itemSort, int pageSize, int pageNumber) {
        var pageable = itemSort.toPageable(pageSize, pageNumber);
        var page = repository.findAll(orderId, pageable);
        var paging = new Paging(pageNumber, pageSize, page.hasNext(), page.hasPrevious());
        return new Items(ItemUi.grouped(page.stream().toList()), paging);
    }

    public void update(Long orderId, Long itemId, ItemAction action) {
        Optional<OrderItem> orderItemOptional = repository.findOrderItemByOrderIdAndItemId(orderId, itemId);
        switch (action) {
            case PLUS -> {
                if (orderItemOptional.isPresent()) {
                    var orderItem = orderItemOptional.get();
                    orderItem.setCount(orderItem.getCount() + 1);
                    update(orderItem);
                } else {
                    insert(new OrderItem(orderId, itemId, 1));
                }
            }
            case MINUS -> {
                if (orderItemOptional.isPresent()) {
                    var orderItem = orderItemOptional.get();
                    if (orderItem.getCount() > 1) {
                        orderItem.setCount(orderItem.getCount() - 1);
                        update(orderItem);
                    } else {
                        deleteById(orderItem.getId());
                    }
                }
            }
            case DELETE -> {
                orderItemOptional.ifPresent(orderItem -> deleteById(orderItem.getId()));
            }
        }
    }

    public OrderItem insert(OrderItem orderItem) {
        return repository.save(orderItem);
    }

    public Optional<OrderItem> findById(Long id) {
        return repository.findById(id);
    }

    public List<ItemUi> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId);
    }

    public Optional<ItemUi> findByOrderIdAndItemId(Long orderId, Long itemId) {
        return repository.findByOrderIdAndItemId(orderId, itemId);
    }

    public OrderItem update(OrderItem orderItem) {
        return repository.save(orderItem);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
