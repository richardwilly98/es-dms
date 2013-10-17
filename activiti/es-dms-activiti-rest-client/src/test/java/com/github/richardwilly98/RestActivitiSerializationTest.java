package com.github.richardwilly98;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.activiti.rest.api.RestProcessInstance;

public class RestActivitiSerializationTest {

    private static Logger log = Logger.getLogger(RestActivitiSerializationTest.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerializeDeserializeProcessInstance() throws Throwable {
        log.debug("*** testSerializeDeserializeProcessInstance ***");
        String id = "instance-" + System.currentTimeMillis();
        RestProcessInstance instance = new RestProcessInstance();
        instance.setProcessDefinitionId("definition-1");
        log.debug(instance);
        String json = mapper.writeValueAsString(instance);
        log.debug(json);
        Assert.assertNotNull(json);
        RestProcessInstance instance2 = mapper.readValue(json, RestProcessInstance.class);
        log.debug(instance2);
        Assert.assertEquals(instance.getProcessDefinitionId(), instance2.getProcessDefinitionId());
        instance2.setId(id);
        json = mapper.writeValueAsString(instance2);
        log.debug(json);
        Assert.assertNotNull(json);
        RestProcessInstance instance3 = mapper.readValue(json, RestProcessInstance.class);
        log.debug(instance3);
        Assert.assertEquals(instance2.getId(), instance3.getId());
    }
}
