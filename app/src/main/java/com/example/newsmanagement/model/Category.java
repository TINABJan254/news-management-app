package com.example.newsmanagement.model;

import java.io.Serializable;

public class Category implements Serializable {
    private String id;   
    private String name; 
    private String description;
    private int articleCount;

    public Category() {
    }

    public Category(String id, String name, String description, int articleCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.articleCount = articleCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }
}
