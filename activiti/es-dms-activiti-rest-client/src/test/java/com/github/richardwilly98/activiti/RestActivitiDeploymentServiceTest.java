package com.github.richardwilly98.activiti;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.activiti.rest.EsDmsServerWithRestActivitiServerBase;
import com.github.richardwilly98.activiti.rest.api.Deployment;
import com.github.richardwilly98.activiti.rest.api.SearchResult;
import com.github.richardwilly98.activiti.rest.client.RestDeploymentServiceClient;

public class RestActivitiDeploymentServiceTest extends EsDmsServerWithRestActivitiServerBase {

    public RestActivitiDeploymentServiceTest() throws Exception {
        super();
    }
    
    @Test
    @org.activiti.engine.test.Deployment(resources={"com/github/richardwilly98/activiti/rest/service/repository/oneTaskProcess.bpmn20.xml"})
    public void testGetDeployments() {
        log.debug("*** testGetDeployments ***");
        RestDeploymentServiceClient client = new RestDeploymentServiceClient(getBaseURI());
        client.setToken(adminToken);
        SearchResult<Deployment> deployments = client.getDeployments();
        log.debug(deployments);
        Assert.assertNotNull(deployments);
        Assert.assertEquals(deployments.getSize(), 1);
        Deployment deployment = deployments.getData().get(0);
        Assert.assertNotNull(deployment);
        log.debug(deployment.getId());
        deployment = client.getDeploment(deployment.getId());
        log.debug(deployment);
    }

    @Test
    @org.activiti.engine.test.Deployment(resources={"com/github/richardwilly98/activiti/rest/service/repository/twoTaskProcess.bpmn20.xml"})
    public void testGetDeployments2() {
        log.debug("*** testGetDeployments ***");
        RestDeploymentServiceClient client = new RestDeploymentServiceClient(getBaseURI());
        client.setToken(adminToken);
        SearchResult<Deployment> deployments = client.getDeployments();
        log.debug(deployments);
        Assert.assertNotNull(deployments);
        Assert.assertEquals(deployments.getSize(), 1);
        Deployment deployment = deployments.getData().get(0);
        Assert.assertNotNull(deployment);
        log.debug(deployment.getId());
        deployment = client.getDeploment(deployment.getId());
        log.debug(deployment);
    }
}
