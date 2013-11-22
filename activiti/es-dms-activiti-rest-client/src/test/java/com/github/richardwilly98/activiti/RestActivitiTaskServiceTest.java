package com.github.richardwilly98.activiti;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.activiti.rest.EsDmsServerWithRestActivitiServerBase;
import com.github.richardwilly98.activiti.rest.api.RestComment;
import com.github.richardwilly98.activiti.rest.api.RestExternalResource;
import com.github.richardwilly98.activiti.rest.api.RestProcessInstance;
import com.github.richardwilly98.activiti.rest.api.RestSearchResult;
import com.github.richardwilly98.activiti.rest.api.RestTask;
import com.github.richardwilly98.activiti.rest.client.RestProcessInstanceServiceClient;
import com.github.richardwilly98.activiti.rest.client.RestTaskServiceClient;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.ProcessServiceProvider;
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
        RestProcessInstanceServiceClient processInstanceClient = new RestProcessInstanceServiceClient(getBaseURI(), ProcessServiceProvider.DEFAULT_REST_TIMEOUT);
        processInstanceClient.setToken(adminToken);
        RestSearchResult<RestProcessInstance> instances = processInstanceClient.getProcessInstances();
        log.debug(instances);
        Assert.assertNotNull(instances);
        Assert.assertEquals(instances.getSize(), 1);
        RestProcessInstance instance = instances.getData().get(0);
        Assert.assertNotNull(instance);
        log.debug(instance.getId());
        instance = processInstanceClient.getProcessInstance(instance.getId());
        Assert.assertNotNull(instance);
        RestTaskServiceClient taskClient = new RestTaskServiceClient(getBaseURI(), ProcessServiceProvider.DEFAULT_REST_TIMEOUT);
        taskClient.setToken(adminToken);
        RestSearchResult<RestTask> tasks = taskClient.getTasks();
        log.debug(tasks);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(tasks.getSize(), 1);
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = { "com/github/richardwilly98/activiti/rest/service/runtime/ProcessInstanceIdentityLinkResourceTest.process.bpmn20.xml" })
    public void testAddCommentToTask() throws ServiceException {
        log.debug("*** testAddCommentToTask ***");
        runtimeService.startProcessInstanceByKey("oneTaskProcess");
        RestTaskServiceClient taskClient = new RestTaskServiceClient(getBaseURI(), ProcessServiceProvider.DEFAULT_REST_TIMEOUT);
        taskClient.setToken(adminToken);
        RestSearchResult<RestTask> tasks = taskClient.getTasks();
        log.debug(tasks);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(tasks.getSize(), 1);
        RestTask task = tasks.getData().get(0);
        RestComment comment = new RestComment();
        comment.setMessage("my message");
        RestComment comment2 = taskClient.addComment(task.getId(), comment);
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
        RestTaskServiceClient taskClient = new RestTaskServiceClient(getBaseURI(), ProcessServiceProvider.DEFAULT_REST_TIMEOUT);
        taskClient.setToken(adminToken);
        RestSearchResult<RestTask> tasks = taskClient.getTasks();
        log.debug(tasks);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(tasks.getSize(), 1);
        RestTask task = tasks.getData().get(0);
        RestExternalResource externalResource = new RestExternalResource();
        externalResource.setName("Simple Attachment - " + System.currentTimeMillis());
        externalResource.setDescription("Simple Attachment description");
        externalResource.setType("simpleType");
        externalResource.setExternalUrl("http://www.google.com?q=activiti");
        RestExternalResource externalResource2 = taskClient.addExternalResource(task.getId(), externalResource);
        Assert.assertNotNull(externalResource2);
        log.debug(externalResource2);
        Assert.assertEquals(externalResource.getName(), externalResource2.getName());
        Assert.assertEquals(externalResource.getType(), externalResource2.getType());
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = { "com/github/richardwilly98/activiti/rest/service/runtime/ProcessInstanceIdentityLinkResourceTest.process.bpmn20.xml" })
    public void testGetTasksByProcessInstance() throws ServiceException {
        log.debug("*** testGetTasksByProcessInstance ***");
        runtimeService.startProcessInstanceByKey("oneTaskProcess");
        RestProcessInstanceServiceClient processInstanceClient = new RestProcessInstanceServiceClient(getBaseURI(), ProcessServiceProvider.DEFAULT_REST_TIMEOUT);
        processInstanceClient.setToken(adminToken);
        RestSearchResult<RestProcessInstance> instances = processInstanceClient.getProcessInstances();
        log.debug(instances);
        Assert.assertNotNull(instances);
        Assert.assertEquals(instances.getSize(), 1);
        RestProcessInstance instance = instances.getData().get(0);
        Assert.assertNotNull(instance);
        log.debug(instance.getId());
        instance = processInstanceClient.getProcessInstance(instance.getId());
        Assert.assertNotNull(instance);
        RestTaskServiceClient taskClient = new RestTaskServiceClient(getBaseURI(), ProcessServiceProvider.DEFAULT_REST_TIMEOUT);
        taskClient.setToken(adminToken);
        RestSearchResult<RestTask> tasks = taskClient.getTasksByProcessInstance(instance.getId());
        log.debug(tasks);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(tasks.getSize(), 1);
    }

    @Test
    @org.activiti.engine.test.Deployment(resources = { "com/github/richardwilly98/activiti/rest/service/runtime/ProcessInstanceIdentityLinkResourceTest.process.bpmn20.xml" })
    public void testSetTaskAssignee() throws ServiceException {
        log.debug("*** testSetTaskAssignee ***");
        runtimeService.startProcessInstanceByKey("oneTaskProcess");
        RestProcessInstanceServiceClient processInstanceClient = new RestProcessInstanceServiceClient(getBaseURI(), ProcessServiceProvider.DEFAULT_REST_TIMEOUT);
        processInstanceClient.setToken(adminToken);
        RestSearchResult<RestProcessInstance> instances = processInstanceClient.getProcessInstances();
        log.debug(instances);
        Assert.assertNotNull(instances);
        Assert.assertEquals(instances.getSize(), 1);
        RestProcessInstance instance = instances.getData().get(0);
        Assert.assertNotNull(instance);
        log.debug(instance.getId());
        instance = processInstanceClient.getProcessInstance(instance.getId());
        Assert.assertNotNull(instance);
        RestTaskServiceClient taskClient = new RestTaskServiceClient(getBaseURI(), ProcessServiceProvider.DEFAULT_REST_TIMEOUT);
        taskClient.setToken(adminToken);
        RestSearchResult<RestTask> tasks = taskClient.getTasksByProcessInstance(instance.getId());
        log.debug(tasks);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(tasks.getSize(), 1);
        RestTask task = tasks.getData().get(0);
        Assert.assertNotNull(task);
        Assert.assertNull(task.getAssignee());
        task.setAssignee(UserService.DEFAULT_ADMIN_LOGIN);
        log.debug("Try to update " + task);
        task = taskClient.update(task);
        Assert.assertEquals(task.getAssignee(), UserService.DEFAULT_ADMIN_LOGIN);
    }
}
