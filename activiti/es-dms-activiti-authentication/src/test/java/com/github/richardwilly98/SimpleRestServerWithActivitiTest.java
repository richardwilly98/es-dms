package com.github.richardwilly98;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.services.UserService;

public class SimpleRestServerWithActivitiTest extends TestRestServerWithActivitiBase {

    public SimpleRestServerWithActivitiTest() throws Exception {
        super();
        log.debug("*** constructor SimpleRestServerWithActivitiTest ***");
    }

    @Test
    @Deployment(resources = { "org/activiti/test/my-test-process.bpmn20.xml" })
    public void testStartProcessInstance() {
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-process");
            Assert.assertNotNull(processInstance);

            Task task = taskService.createTaskQuery().singleResult();
            Assert.assertEquals("Activiti is awesome!", task.getName());
        } catch (Throwable t) {
            log.error("testStartProcessInstance failed", t);
            Assert.fail();
        }
    }

    @Test
    public void testAuthentication() {
        boolean authenticated = identityService.checkPassword(UserService.DEFAULT_ADMIN_LOGIN, UserService.DEFAULT_ADMIN_PASSWORD);
        Assert.assertEquals(authenticated, true);
    }
}
