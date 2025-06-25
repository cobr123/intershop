package com.example.intershop.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "order_items")
public class OrderItem {
    @Id
    private Long id;
    @Column
    private Long orderId;
    @Column
    private Long itemId;
    private Integer count;

    public OrderItem() {
    }

    public OrderItem(Long orderId, Long itemId, Integer count) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
