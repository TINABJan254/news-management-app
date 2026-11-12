package com.example.newsmanagement.model;

import java.io.Serializable;

public class News implements Serializable {
    private String id;
    private String title;
    private String detail;
    private String imageUrl;
    private Category category;
    private String author;
    private String publishDate;

    public News() {
    }

    public News(String id, String title, String detail, String imageUrl, Category category, String author, String publishDate) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.imageUrl = imageUrl;
        this.category = category;
        this.author = author;
        this.publishDate = publishDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getCategoryId() {
        return category != null ? category.getId() : "";
    }

    public String getCategoryName() {
        return category != null ? category.getName() : "";
    }

    @Override
    public String toString() {
        return "News{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category=" + (category != null ? category.getName() : "null") +
                ", author='" + author + '\'' +
                ", publishDate='" + publishDate + '\'' +
                '}';
    }
}
