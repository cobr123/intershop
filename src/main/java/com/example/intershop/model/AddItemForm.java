package com.example.intershop.model;

import org.springframework.http.codec.multipart.FilePart;

import java.math.BigDecimal;

public class AddItemForm {
    private String title;
    private String description;
    private FilePart image;
    private BigDecimal price;

    public AddItemForm() {
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

    public FilePart getImage() {
        return image;
    }

    public void setImage(FilePart image) {
        this.image = image;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
