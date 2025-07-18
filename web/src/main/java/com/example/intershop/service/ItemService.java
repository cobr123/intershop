package com.example.intershop.service;

import com.example.intershop.model.*;
import com.example.intershop.repository.ItemRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ItemService {

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "items", key = "#id")
    public Mono<Item> findById(Long id) {
        return repository.findById(id);
    }

    @Caching(
            evict = {@CacheEvict(value = "paged_order_items", allEntries = true)},
            put = {@CachePut(value = "items", key = "#result.id")}
    )
    public Mono<Item> insert(Item item) {
        return repository.save(item);
    }

    @Caching(
            evict = {@CacheEvict(value = "paged_order_items", allEntries = true)},
            put = {@CachePut(value = "items", key = "#item.id")}
    )
    public Mono<Item> update(Item item) {
        return repository.save(item);
    }

    @Caching(evict = {
            @CacheEvict(value = "paged_order_items", allEntries = true),
            @CacheEvict(value = "items", key = "#id")
    })
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
}
