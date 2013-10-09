package com.github.richardwilly98.activiti;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/*
 * DEBUG [com.githug.richardwilly98.ActivityRestDeploymentServiceTest] {"data":[{"id":"20","name":"Demo processes","deploymentTime":"2013-10-02T16:21:15EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/20"},{"id":"2330","name":"FinancialReportProcess.bpmn20.xml","deploymentTime":"2013-10-02T17:09:08EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/2330"},{"id":"604","name":"Demo reports","deploymentTime":"2013-10-02T16:25:52EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/604"}],"total":3,"start":0,"sort":"id","order":"asc","size":3} 
 */
public class Task extends ItemBase {

    private String owner;
    private String assignee;
    private boolean delegationState;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date createTime;
    private Date dueDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private int priority;
    private boolean suspended;
    private String taskDefinitionKey;
    private String parentTaskId;
    private String parentTaskUrl;
    private String executionId;
    private String executionUrl;
    private String processInstanceId;
    private String processInstanceUrl;
    private String processDefinitionId;
    private String processDefinitionUrl;
    private Object variables;

    public Task() {

    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public boolean isDelegationState() {
        return delegationState;
    }

    public void setDelegationState(boolean delegationState) {
        this.delegationState = delegationState;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public String getParentTaskUrl() {
        return parentTaskUrl;
    }

    public void setParentTaskUrl(String parentTaskUrl) {
        this.parentTaskUrl = parentTaskUrl;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getExecutionUrl() {
        return executionUrl;
    }

    public void setExecutionUrl(String executionUrl) {
        this.executionUrl = executionUrl;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessInstanceUrl() {
        return processInstanceUrl;
    }

    public void setProcessInstanceUrl(String processInstanceUrl) {
        this.processInstanceUrl = processInstanceUrl;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessDefinitionUrl() {
        return processDefinitionUrl;
    }

    public void setProcessDefinitionUrl(String processDefinitionUrl) {
        this.processDefinitionUrl = processDefinitionUrl;
    }

    public Object getVariables() {
        return variables;
    }

    public void setVariables(Object variables) {
        this.variables = variables;
    }

}
