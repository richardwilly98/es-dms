package com.github.richardwilly98.activiti;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.activiti.rest.EsDmsServerWithRestActivitiServerBase;
import com.github.richardwilly98.activiti.rest.api.ProcessDefinition;
import com.github.richardwilly98.activiti.rest.api.SearchResult;
import com.github.richardwilly98.activiti.rest.client.RestProcessDefinitionServiceClient;

public class RestActivitiProcessDefinitionServiceTest extends EsDmsServerWithRestActivitiServerBase {

    public RestActivitiProcessDefinitionServiceTest() throws Exception {
        super();
    }
    
    @Test
    @org.activiti.engine.test.Deployment(resources={"com/github/richardwilly98/activiti/rest/service/repository/SimpleProcessDefinitionResource.bpmn20.xml"})
    public void testGetProcessDefinitions() {
        log.debug("*** testGetProcessDefinitions ***");
        RestProcessDefinitionServiceClient client = new RestProcessDefinitionServiceClient(getBaseURI());
        client.setToken(adminToken);
        SearchResult<ProcessDefinition> definitions = client.getProcessDefinitions();
        Assert.assertNotNull(definitions);
        log.debug(definitions);
        Assert.assertEquals(definitions.getSize(), 1);
        ProcessDefinition definition = definitions.getData().get(0);
        Assert.assertNotNull(definition);
        log.debug(definition.getId());
        definition = client.getProcessDefinition(definition.getId());
        Assert.assertNotNull(definition);
    }

}
