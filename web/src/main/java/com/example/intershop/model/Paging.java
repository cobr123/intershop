package com.example.intershop.model;

public record Paging(int pageNumber, int pageSize, boolean hasNext, boolean hasPrevious) {
}