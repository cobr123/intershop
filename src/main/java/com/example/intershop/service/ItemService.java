package com.example.intershop.service;

import com.example.intershop.model.Item;
import com.example.intershop.model.ItemSort;
import com.example.intershop.model.Items;
import com.example.intershop.model.Paging;
import com.example.intershop.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public Items findByTitleLikeOrDescriptionLike(String search, ItemSort itemSort, int pageSize, int pageNumber, int lineSize) {
        if (search.isBlank()) {
            return findAll(itemSort, pageSize, pageNumber, lineSize);
        } else {
            var pageable = itemSort.toPageable(pageSize, pageNumber);
            var searchLike = "%" + search + "%";
            var page = repository.findByTitleLikeOrDescriptionLike(searchLike, searchLike, pageable);
            var paging = new Paging(pageNumber, pageSize, page.hasNext(), page.hasPrevious());
            return new Items(grouped(page.stream().toList(), lineSize), paging);
        }
    }

    public Items findAll(ItemSort itemSort, int pageSize, int pageNumber, int lineSize) {
        var pageable = itemSort.toPageable(pageSize, pageNumber);
        var page = repository.findAll(pageable);
        var paging = new Paging(pageNumber, pageSize, page.hasNext(), page.hasPrevious());
        return new Items(grouped(page.stream().toList(), lineSize), paging);
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

    public List<List<Item>> grouped(List<Item> list, int lineSize) {
        var result = new ArrayList<List<Item>>();
        var acc = new ArrayList<Item>();
        for (Item item : list) {
            acc.add(item);
            if (acc.size() >= lineSize) {
                result.add(acc);
                acc = new ArrayList<>();
            }
        }
        if (!acc.isEmpty()) {
            result.add(acc);
        }
        return result;
    }

}
