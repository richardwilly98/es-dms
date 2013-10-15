package com.github.richardwilly98.activiti.rest.api;

public class Comment extends ItemBase {

    private String author;
    private String message;

    public Comment() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
