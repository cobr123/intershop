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

    public Items findAll(ItemSort itemSort, int pageSize, int pageNumber) {
        var pageable = itemSort.toPageable(pageSize, pageNumber);
        var page = repository.findAll(pageable);
        var paging = new Paging(pageNumber, pageSize, page.hasNext(), page.hasPrevious());
        return new Items(ItemUi.grouped(page.stream().map(ItemUi::new).toList()), paging);
    }
}
