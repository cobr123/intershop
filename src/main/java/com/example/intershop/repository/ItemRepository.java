package com.example.intershop.repository;

import com.example.intershop.model.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ItemRepository extends CrudRepository<Item, Long>, PagingAndSortingRepository<Item, Long> {
}
