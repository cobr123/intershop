package com.example.intershop.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column
    private String description;
    @Column
    private String imgPath;
    @Column(nullable = false)
    private Integer count;
    @Column(nullable = false)
    private BigDecimal price;

    public Item() {
    }

    public Item(String title, Integer count, BigDecimal price) {
        this.title = title;
        this.count = count;
        this.price = price;
    }

    public Item(Long id, String title, String description, String imgPath, Integer count, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.count = count;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
