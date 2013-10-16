package com.github.richardwilly98.activiti.rest.api;

import com.google.common.base.Objects;

public class RestComment extends RestItemBase {

    private String author;
    private String message;

    public RestComment() {
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

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("author", author).add("message", message).toString();
    }
}
