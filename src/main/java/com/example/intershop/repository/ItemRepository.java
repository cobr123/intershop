package com.example.intershop.repository;

import com.example.intershop.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ItemRepository extends CrudRepository<Item, Long>, PagingAndSortingRepository<Item, Long> {
    Page<Item> findByTitleLikeOrDescriptionLike(String title, String description, Pageable pageable);
}
