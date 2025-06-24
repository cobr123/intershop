package com.example.intershop.model;

import jakarta.validation.constraints.Min;

public class SearchForm {
    private String search = "";
    private ItemSort itemSort = ItemSort.NO;
    @Min(1)
    private int pageSize = 10;
    @Min(1)
    private int pageNumber = 1;

    public SearchForm() {
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public ItemSort getItemSort() {
        return itemSort;
    }

    public void setItemSort(ItemSort itemSort) {
        this.itemSort = itemSort;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
