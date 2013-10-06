package com.github.richardwilly98;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.activiti.Deployment;
import com.github.richardwilly98.activiti.SearchResult;

public class ActivityRestDeploymentServiceTest extends ActivitiRestClientTest {

	@Test
	public void testRetrieveDeployments() {
		log.debug("*** testRetrieveDeployments ***");
		Response response = target().path("repository/deployments").request().get();
		log.debug("Response status: " + response.getStatus());
		Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
		SearchResult<Deployment> deploymentList = response.readEntity(new GenericType<SearchResult<Deployment>>() {
	    });
		Assert.assertNotNull(deploymentList);
	}

}
