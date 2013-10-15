package com.github.richardwilly98;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.activiti.rest.api.ProcessDefinition;
import com.github.richardwilly98.activiti.rest.api.ProcessInstance;
import com.github.richardwilly98.activiti.rest.api.SearchResult;

public class ActivityRestRuntimeProcessServiceTest extends ActivitiRestClientTest {

    @Test(enabled = false)
    public void testRetrieveProcessDefinition() {
        log.debug("*** testRetrieveProcessDefinition ***");
        String id = "financialReport:1:2345";
        Response response = target().path("repository/process-definitions").path(id).request().get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        ProcessDefinition processDefinition = response.readEntity(ProcessDefinition.class);
        Assert.assertNotNull(processDefinition);
        log.debug(processDefinition);
    }

    @Test(enabled = false)
    public void testRetrieveProcessInstance() {
        log.debug("*** testRetrieveProcessInstance ***");
        Response response = target().path("runtime/process-instances").request().get();
        SearchResult<ProcessInstance> processInstanceList = response.readEntity(new GenericType<SearchResult<ProcessInstance>>() {
        });
        Assert.assertNotNull(processInstanceList);
        for (ProcessInstance processInstance : processInstanceList.getData()) {
            log.debug(processInstance);
        }
    }

    @Test(enabled = false)
    public void testStartProcessInstance() {
        log.debug("*** testStartProcessInstance ***");
        String id = "financialReport:1:2345";
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setProcessDefinitionId(id);
        Response response = target().path("runtime/process-instances").request()
                .post(Entity.entity(processInstance, MediaType.APPLICATION_JSON));
        processInstance = response.readEntity(ProcessInstance.class);
        Assert.assertNotNull(processInstance);
        log.debug(processInstance);
    }
}
