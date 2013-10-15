package com.github.richardwilly98.activiti;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.activiti.rest.EsDmsServerWithRestActivitiServerBase;
import com.github.richardwilly98.activiti.rest.api.ProcessDefinition;
import com.github.richardwilly98.activiti.rest.api.ProcessInstance;
import com.github.richardwilly98.activiti.rest.api.SearchResult;
import com.github.richardwilly98.activiti.rest.client.RestProcessDefinitionServiceClient;
import com.github.richardwilly98.activiti.rest.client.RestProcessInstanceServiceClient;

public class RestActivitiProcessInstanceServiceTest extends EsDmsServerWithRestActivitiServerBase {

//    private String url = "http://localhost:" + REST_PORT;

    public RestActivitiProcessInstanceServiceTest() throws Exception {
        super();
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = { "com/github/richardwilly98/activiti/rest/service/runtime/ProcessInstanceIdentityLinkResourceTest.process.bpmn20.xml" })
    public void testGetProcessInstances() {
        log.debug("*** testGetProcessInstances ***");
        runtimeService.startProcessInstanceByKey("oneTaskProcess");
        runtimeService.startProcessInstanceByKey("oneTaskProcess");
        RestProcessInstanceServiceClient client = new RestProcessInstanceServiceClient(getBaseURI());
        client.setToken(adminToken);
        SearchResult<ProcessInstance> instances = client.getProcessInstances();
        log.debug(instances);
        Assert.assertNotNull(instances);
        Assert.assertEquals(instances.getSize(), 2);
        ProcessInstance instance = instances.getData().get(0);
        Assert.assertNotNull(instance);
        log.debug(instance.getId());
        instance = client.getProcessInstance(instance.getId());
        Assert.assertNotNull(instance);
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = { "com/github/richardwilly98/activiti/rest/service/runtime/ProcessInstanceIdentityLinkResourceTest.process.bpmn20.xml" })
    public void testStartProcessInstance() {
        log.debug("*** testStartProcessInstance ***");
        RestProcessDefinitionServiceClient processDefinitionClient = new RestProcessDefinitionServiceClient(getBaseURI());
        processDefinitionClient.setToken(adminToken);
        SearchResult<ProcessDefinition> definitions = processDefinitionClient.getProcessDefinitions();
        Assert.assertNotNull(definitions);
        Assert.assertEquals(definitions.getSize(), 1);
        ProcessDefinition definition = definitions.getData().get(0);
        Assert.assertNotNull(definition);
        RestProcessInstanceServiceClient processInstanceClient = new RestProcessInstanceServiceClient(getBaseURI());
        processInstanceClient.setToken(adminToken);
        ProcessInstance instance1 = processInstanceClient.startProcessInstance(definition.getId());
        Assert.assertNotNull(instance1);
        log.debug(instance1);
        Assert.assertEquals(instance1.getProcessDefinitionId(), definition.getId());
        SearchResult<ProcessInstance> instances = processInstanceClient.getProcessInstances();
        log.debug(instances);
        ProcessInstance instance2 = processInstanceClient.getProcessInstance(instance1.getId());
        Assert.assertNotNull(instance2);
        log.debug(instance2);
        Assert.assertEquals(instance1.getId(), instance2.getId());
    }
}
