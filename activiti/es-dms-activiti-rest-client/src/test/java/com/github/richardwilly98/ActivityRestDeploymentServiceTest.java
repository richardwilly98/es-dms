package com.github.richardwilly98;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.activiti.rest.api.RestDeployment;
import com.github.richardwilly98.activiti.rest.api.RestSearchResult;

public class ActivityRestDeploymentServiceTest extends ActivitiRestClientTest {

    @Test(enabled = false)
    public void testRetrieveDeployments() {
        log.debug("*** testRetrieveDeployments ***");
        Response response = target().path("repository/deployments").request().get();
        log.debug("Response status: " + response.getStatus());
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        RestSearchResult<RestDeployment> deploymentList = response.readEntity(new GenericType<RestSearchResult<RestDeployment>>() {
        });
        Assert.assertNotNull(deploymentList);
    }

}
