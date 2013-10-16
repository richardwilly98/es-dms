package com.github.richardwilly98.activiti.rest.api;

import com.google.common.base.Objects;

/*
 * DEBUG [com.githug.richardwilly98.ActivityRestDeploymentServiceTest] {"data":[{"id":"20","name":"Demo processes","deploymentTime":"2013-10-02T16:21:15EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/20"},{"id":"2330","name":"FinancialReportProcess.bpmn20.xml","deploymentTime":"2013-10-02T17:09:08EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/2330"},{"id":"604","name":"Demo reports","deploymentTime":"2013-10-02T16:25:52EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/604"}],"total":3,"start":0,"sort":"id","order":"asc","size":3} 
 */
public class RestProcessInstance extends RestItemBase {

    private String businessKey;
    private boolean suspended;
    private String processDefinitionId;
    private String processDefinitionUrl;
    private String activityId;
    private Object variables;

    public RestProcessInstance() {

    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
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

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Object getVariables() {
        return variables;
    }

    public void setVariables(Object variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", getId()).add("name", getName()).add("description", getDescription())
                .add("url", getUrl()).add("businessKey", businessKey).add("suspended", suspended)
                .add("processDefinitionId", processDefinitionId).add("processDefinitionUrl", processDefinitionUrl)
                .add("activityId", activityId).add("variables", variables).toString();
    }
}
