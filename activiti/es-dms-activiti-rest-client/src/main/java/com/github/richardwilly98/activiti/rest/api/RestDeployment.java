package com.github.richardwilly98.activiti.rest.api;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Objects;

/*
 * DEBUG [com.githug.richardwilly98.ActivityRestDeploymentServiceTest] {"data":[{"id":"20","name":"Demo processes","deploymentTime":"2013-10-02T16:21:15EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/20"},{"id":"2330","name":"FinancialReportProcess.bpmn20.xml","deploymentTime":"2013-10-02T17:09:08EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/2330"},{"id":"604","name":"Demo reports","deploymentTime":"2013-10-02T16:25:52EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/604"}],"total":3,"start":0,"sort":"id","order":"asc","size":3} 
 */
public class RestDeployment extends RestItemBase {

    public Date getDeploymentTime() {
        return deploymentTime;
    }

    public void setDeploymentTime(Date deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date deploymentTime;
    private String category;

    public RestDeployment() {

    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", getId()).add("name", getName()).add("description", getDescription())
                .add("url", getUrl()).add("deploymentTime", deploymentTime).add("category", category).toString();
    }

}
