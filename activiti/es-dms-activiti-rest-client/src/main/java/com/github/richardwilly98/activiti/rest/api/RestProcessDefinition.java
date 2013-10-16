package com.github.richardwilly98.activiti.rest.api;

import com.google.common.base.Objects;

/*
 * DEBUG [com.githug.richardwilly98.ActivityRestDeploymentServiceTest] {"data":[{"id":"20","name":"Demo processes","deploymentTime":"2013-10-02T16:21:15EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/20"},{"id":"2330","name":"FinancialReportProcess.bpmn20.xml","deploymentTime":"2013-10-02T17:09:08EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/2330"},{"id":"604","name":"Demo reports","deploymentTime":"2013-10-02T16:25:52EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/604"}],"total":3,"start":0,"sort":"id","order":"asc","size":3} 
 */
public class RestProcessDefinition extends RestItemBase {

    private String key;
    private int version;
    private String deploymentId;
    private String deploymentUrl;
    private String resource;
    private String diagramResource;
    private String category;
    private boolean graphicalNotationDefined;
    private boolean suspended;
    private boolean startFormDefined;

    public RestProcessDefinition() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getDeploymentUrl() {
        return deploymentUrl;
    }

    public void setDeploymentUrl(String deploymentUrl) {
        this.deploymentUrl = deploymentUrl;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getDiagramResource() {
        return diagramResource;
    }

    public void setDiagramResource(String diagramResource) {
        this.diagramResource = diagramResource;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isGraphicalNotationDefined() {
        return graphicalNotationDefined;
    }

    public void setGraphicalNotationDefined(boolean graphicalNotationDefined) {
        this.graphicalNotationDefined = graphicalNotationDefined;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isStartFormDefined() {
        return startFormDefined;
    }

    public void setStartFormDefined(boolean startFormDefined) {
        this.startFormDefined = startFormDefined;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", getId()).add("name", getName()).add("description", getDescription())
                .add("url", getUrl()).add("key", key).add("version", version).add("deploymentId", deploymentId)
                .add("deploymentUrl", deploymentUrl).add("resource", resource).add("diagramResource", diagramResource)
                .add("category", category).add("graphicalNotationDefined", graphicalNotationDefined).add("suspended", suspended)
                .add("startFormDefined", startFormDefined).toString();
    }
}
