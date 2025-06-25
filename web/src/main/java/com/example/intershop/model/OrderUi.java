package com.example.intershop.model;

import java.math.BigDecimal;
import java.util.List;

public record OrderUi(Long id, List<ItemUi> items) {

    public BigDecimal totalSum() {
        return items.stream().map(i -> BigDecimal.valueOf(i.getCount()).multiply(i.getPrice())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }
}