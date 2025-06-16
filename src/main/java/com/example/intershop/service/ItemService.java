package com.example.intershop.service;

import com.example.intershop.model.Item;
import com.example.intershop.model.ItemSort;
import com.example.intershop.model.Items;
import com.example.intershop.model.Paging;
import com.example.intershop.repository.ItemRepository;
import org.springframework.data.domain.Pageable;
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

    public Items findByTitleLikeOrDescriptionLike(String search, ItemSort itemSort, int pageSize, int pageNumber) {
        if (search.isBlank()) {
            return findAll(itemSort, pageSize, pageNumber);
        } else {
            var pageable = itemSort.toPageable(pageSize, pageNumber);
            var page = repository.findByTitleLikeOrDescriptionLike("%" + search + "%", search, pageable);
            var paging = new Paging(pageNumber, pageSize, page.hasNext(), page.hasPrevious());
            return new Items(page.stream().toList(), paging);
        }
    }

    public Items findAll(ItemSort itemSort, int pageSize, int pageNumber) {
        var pageable = itemSort.toPageable(pageSize, pageNumber);
        var page = repository.findAll(pageable);
        var paging = new Paging(pageNumber, pageSize, page.hasNext(), page.hasPrevious());
        return new Items(page.stream().toList(), paging);
    }

    public Item insert(Item item) {
        return repository.save(item);
    }

    public void update(Item item) {
        repository.save(item);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}
