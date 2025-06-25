package com.example.intershop.model;

import java.util.List;

public record Items(List<List<ItemUi>> items, Paging paging) {
}
