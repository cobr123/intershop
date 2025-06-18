package com.example.intershop.service;

import com.example.intershop.model.*;
import com.example.intershop.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public Optional<Item> findById(Long id) {
        return repository.findById(id);
    }

    public Item insert(Item item) {
        return repository.save(item);
    }

    public Item update(Item item) {
        return repository.save(item);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
