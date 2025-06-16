package com.example.intershop.model;

import java.util.List;

public record Items(List<Item> items, Paging paging) {
}
