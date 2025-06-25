package com.example.intershop.service;

import com.example.intershop.model.*;
import com.example.intershop.repository.ItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ItemService {

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public Mono<Item> findById(Long id) {
        return repository.findById(id);
    }

    public Mono<Item> insert(Item item) {
        return repository.save(item);
    }

    public Mono<Item> update(Item item) {
        return repository.save(item);
    }

    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
}
