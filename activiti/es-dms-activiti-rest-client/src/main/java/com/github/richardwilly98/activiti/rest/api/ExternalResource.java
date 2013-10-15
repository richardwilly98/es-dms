package com.github.richardwilly98.activiti.rest.api;

public class ExternalResource extends ItemBase {

    private String type;
    private String externalUrl;
    private String taskUrl;
    private String processInstanceUrl;
    private String contentUrl;

    public ExternalResource() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getTaskUrl() {
        return taskUrl;
    }

    public void setTaskUrl(String taskUrl) {
        this.taskUrl = taskUrl;
    }

    public String getProcessInstanceUrl() {
        return processInstanceUrl;
    }

    public void setProcessInstanceUrl(String processInstanceUrl) {
        this.processInstanceUrl = processInstanceUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}
