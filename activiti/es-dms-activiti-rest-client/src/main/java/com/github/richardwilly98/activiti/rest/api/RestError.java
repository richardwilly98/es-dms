package com.github.richardwilly98.activiti.rest.api;

import com.google.common.base.Objects;

public class RestError {

    private int statusCode;
    private String errorMessage;

    public RestError() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("statusCode", statusCode).add("errorMessage", errorMessage).toString();
    }
}
