package com.github.richardwilly98.activiti;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.activiti.rest.EsDmsServerWithRestActivitiServerBase;
import com.github.richardwilly98.activiti.rest.api.Comment;
import com.github.richardwilly98.activiti.rest.api.ExternalResource;
import com.github.richardwilly98.activiti.rest.api.ProcessInstance;
import com.github.richardwilly98.activiti.rest.api.SearchResult;
import com.github.richardwilly98.activiti.rest.api.Task;
import com.github.richardwilly98.activiti.rest.client.RestProcessInstanceServiceClient;
import com.github.richardwilly98.activiti.rest.client.RestTaskServiceClient;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.UserService;

public class RestActivitiTaskServiceTest extends EsDmsServerWithRestActivitiServerBase {

    public RestActivitiTaskServiceTest() throws Exception {
        super();
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = { "com/github/richardwilly98/activiti/rest/service/runtime/ProcessInstanceIdentityLinkResourceTest.process.bpmn20.xml" })
    public void testGetTasks() throws ServiceException {
        log.debug("*** testGetTasks ***");
        runtimeService.startProcessInstanceByKey("oneTaskProcess");
        RestProcessInstanceServiceClient processInstanceClient = new RestProcessInstanceServiceClient(getBaseURI());
        processInstanceClient.setToken(adminToken);
        SearchResult<ProcessInstance> instances = processInstanceClient.getProcessInstances();
        log.debug(instances);
        Assert.assertNotNull(instances);
        Assert.assertEquals(instances.getSize(), 1);
        ProcessInstance instance = instances.getData().get(0);
        Assert.assertNotNull(instance);
        log.debug(instance.getId());
        instance = processInstanceClient.getProcessInstance(instance.getId());
        Assert.assertNotNull(instance);
        RestTaskServiceClient taskClient = new RestTaskServiceClient(getBaseURI());
        taskClient.setToken(adminToken);
        SearchResult<Task> tasks = taskClient.getTasks();
        log.debug(tasks);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(tasks.getSize(), 1);
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = { "com/github/richardwilly98/activiti/rest/service/runtime/ProcessInstanceIdentityLinkResourceTest.process.bpmn20.xml" })
    public void testAddCommentToTask() throws ServiceException {
        log.debug("*** testAddCommentToTask ***");
        runtimeService.startProcessInstanceByKey("oneTaskProcess");
        RestTaskServiceClient taskClient = new RestTaskServiceClient(getBaseURI());
        taskClient.setToken(adminToken);
        SearchResult<Task> tasks = taskClient.getTasks();
        log.debug(tasks);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(tasks.getSize(), 1);
        Task task = tasks.getData().get(0);
        Comment comment = new Comment();
        comment.setMessage("my message");
        Comment comment2 = taskClient.addComment(task.getId(), comment);
        Assert.assertNotNull(comment2);
        log.debug(comment2);
        Assert.assertEquals(UserService.DEFAULT_ADMIN_LOGIN, comment2.getAuthor());
        Assert.assertEquals(comment.getMessage(), comment2.getMessage());
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = { "com/github/richardwilly98/activiti/rest/service/runtime/ProcessInstanceIdentityLinkResourceTest.process.bpmn20.xml" })
    public void testAddExternalResourceToTask() throws ServiceException {
        log.debug("*** testAddExternalResourceToTask ***");
        runtimeService.startProcessInstanceByKey("oneTaskProcess");
        RestTaskServiceClient taskClient = new RestTaskServiceClient(getBaseURI());
        taskClient.setToken(adminToken);
        SearchResult<Task> tasks = taskClient.getTasks();
        log.debug(tasks);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(tasks.getSize(), 1);
        Task task = tasks.getData().get(0);
        ExternalResource externalResource = new ExternalResource();
        externalResource.setName("Simple Attachment - " + System.currentTimeMillis());
        externalResource.setDescription("Simple Attachment description");
        externalResource.setType("simpleType");
        externalResource.setExternalUrl("http://www.google.com?q=activiti");
        ExternalResource externalResource2 = taskClient.addExternalResource(task.getId(), externalResource);
        Assert.assertNotNull(externalResource2);
        log.debug(externalResource2);
        Assert.assertEquals(externalResource.getName(), externalResource2.getName());
        Assert.assertEquals(externalResource.getType(), externalResource2.getType());
    }
}
