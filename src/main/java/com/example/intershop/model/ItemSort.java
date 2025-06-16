package com.example.intershop.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public enum ItemSort {
    NO, ALPHA, PRICE;

    public Sort toSort() {
        return switch (this) {
            case NO -> Sort.unsorted();
            case ALPHA -> Sort.by("title");
            case PRICE -> Sort.by("price");
        };
    }

    public Pageable toPageable(int pageSize, int pageNumber) {
        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber - 1);
        pageable.getSort().and(toSort());
        return pageable;
    }
}
