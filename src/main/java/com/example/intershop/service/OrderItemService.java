package com.example.intershop.service;

import com.example.intershop.model.Item;
import com.example.intershop.model.ItemAction;
import com.example.intershop.model.OrderItem;
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

    public void update(Long orderId, Long itemId, ItemAction action) {
        Optional<OrderItem> orderItemOptional = repository.findByOrderIdAndItemId(orderId, itemId);
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

    public List<Item> findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId);
    }

    public OrderItem update(OrderItem orderItem) {
        return repository.save(orderItem);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
