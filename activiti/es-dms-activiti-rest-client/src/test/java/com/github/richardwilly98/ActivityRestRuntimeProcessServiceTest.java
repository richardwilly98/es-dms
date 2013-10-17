package com.github.richardwilly98;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.activiti.rest.api.RestProcessDefinition;
import com.github.richardwilly98.activiti.rest.api.RestProcessInstance;
import com.github.richardwilly98.activiti.rest.api.RestSearchResult;

public class ActivityRestRuntimeProcessServiceTest extends ActivitiRestClientTest {

    @Test(enabled = false)
    public void testRetrieveProcessDefinition() {
        log.debug("*** testRetrieveProcessDefinition ***");
        String id = "financialReport:1:2345";
        Response response = target().path("repository/process-definitions").path(id).request().get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        RestProcessDefinition processDefinition = response.readEntity(RestProcessDefinition.class);
        Assert.assertNotNull(processDefinition);
        log.debug(processDefinition);
    }

    @Test(enabled = false)
    public void testRetrieveProcessInstance() {
        log.debug("*** testRetrieveProcessInstance ***");
        Response response = target().path("runtime/process-instances").request().get();
        RestSearchResult<RestProcessInstance> processInstanceList = response.readEntity(new GenericType<RestSearchResult<RestProcessInstance>>() {
        });
        Assert.assertNotNull(processInstanceList);
        for (RestProcessInstance processInstance : processInstanceList.getData()) {
            log.debug(processInstance);
        }
    }

    @Test(enabled = false)
    public void testStartProcessInstance() {
        log.debug("*** testStartProcessInstance ***");
        String id = "financialReport:1:2345";
        RestProcessInstance processInstance = new RestProcessInstance();
        processInstance.setProcessDefinitionId(id);
        Response response = target().path("runtime/process-instances").request()
                .post(Entity.entity(processInstance, MediaType.APPLICATION_JSON));
        processInstance = response.readEntity(RestProcessInstance.class);
        Assert.assertNotNull(processInstance);
        log.debug(processInstance);
    }
}
