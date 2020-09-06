package com.example.popularmovies.DataBase;

public class Review {
    private String id;
    private String parentId;
    private String auther;
    private String content;

    public Review(String id, String auther, String content) {
        this.id = id;
        this.auther = auther;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuther() {
        return auther;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
