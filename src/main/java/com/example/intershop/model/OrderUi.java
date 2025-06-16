package com.example.intershop.model;

import java.util.List;

public record OrderUi(Long id, List<Item> items) {
}