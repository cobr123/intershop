package com.example.intershop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ItemUi {
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private BigDecimal price;
    private Integer count = 0;

    public ItemUi() {
    }

    public ItemUi(Long id, String title, String description, String imgPath, Integer count, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imgPath = imgPath;
        this.price = price;
        this.count = count;
    }

    public ItemUi(Item item) {
        id = item.getId();
        title = item.getTitle();
        description = item.getDescription();
        imgPath = item.getImgPath();
        price = item.getPrice();
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


    public static List<List<ItemUi>> grouped(List<ItemUi> list) {
        var lineSize = 3;
        var result = new ArrayList<List<ItemUi>>();
        var acc = new ArrayList<ItemUi>();
        for (var item : list) {
            acc.add(item);
            if (acc.size() >= lineSize) {
                result.add(acc);
                acc = new ArrayList<>();
            }
        }
        if (!acc.isEmpty()) {
            result.add(acc);
        }
        return result;
    }
}
