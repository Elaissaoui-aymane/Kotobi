package com.example.kotobi;

import java.sql.Array;

public class Books {
    public String id;
    public String name;
    public String description;
    public double price;
    public String imageUrl;
    public String author;
    public String type;
    private String fileUrl;

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    // constructeur obligé pour appele DataSnapshot.getValue(Product.class)
    public Books() {
    }

    public Books(String id, String name, String description, double price, String imageUrl,String Author,String type,String fileUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.author = Author;
        this.type = type;
        this.fileUrl = fileUrl;
    }
}
