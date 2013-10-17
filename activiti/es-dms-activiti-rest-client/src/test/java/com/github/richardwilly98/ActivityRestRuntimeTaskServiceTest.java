package com.github.richardwilly98;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import com.github.richardwilly98.activiti.rest.api.RestComment;
import com.github.richardwilly98.activiti.rest.api.RestExternalResource;
import com.github.richardwilly98.activiti.rest.api.RestSearchResult;
import com.github.richardwilly98.activiti.rest.api.RestTask;

public class ActivityRestRuntimeTaskServiceTest extends ActivitiRestClientTest {

    @Test(enabled = false)
    public void testCleanUpTasks() {
        log.debug("*** testCleanUpTasks ***");
        Response response = target().path("runtime/tasks").queryParam("size", 100).request().get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        RestSearchResult<RestTask> taskList = response.readEntity(new GenericType<RestSearchResult<RestTask>>() {
        });
        Assert.assertNotNull(taskList);
        for (RestTask task : taskList.getData()) {
            if (Strings.isNullOrEmpty(task.getProcessInstanceId())) {
                log.debug(String.format("Delete task %s", task.getId()));
                target().path("runtime/tasks").path(task.getId()).request().delete();
            }
        }
    }

    @Test(enabled = false)
    public void testRetrieveTasks() {
        log.debug("*** testRetrieveTasks ***");
        Response response = target().path("runtime/tasks").request().get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        RestSearchResult<RestTask> taskList = response.readEntity(new GenericType<RestSearchResult<RestTask>>() {
        });
        Assert.assertNotNull(taskList);
        for (RestTask task : taskList.getData()) {
            log.debug(task);
        }
    }

    @Test(enabled = false)
    public void testNewTasks() {
        log.debug("*** testNewTasks ***");
        Response response = target().path("runtime/tasks").request().get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        RestSearchResult<RestTask> taskList = response.readEntity(new GenericType<RestSearchResult<RestTask>>() {
        });
        Assert.assertNotNull(taskList);
        for (RestTask task : taskList.getData()) {
            log.debug(task);
        }
    }

    @Test(enabled = false)
    public void testAddComment() {
        log.debug("*** testAddComment ***");
        Response response = target().path("runtime/tasks").request().get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        RestSearchResult<RestTask> taskList = response.readEntity(new GenericType<RestSearchResult<RestTask>>() {
        });
        Assert.assertNotNull(taskList);
        String id = null;
        for (RestTask task : taskList.getData()) {
            log.debug(task);
            id = task.getId();
            break;
        }
        id = "2340";
        Assert.assertNotNull(id);
        RestComment comment = new RestComment();
        comment.setAuthor(ADMIN_USERNAME);
        comment.setMessage("This is my first comment");
        response = target().path("runtime/tasks").path(id).path("comments").request()
                .post(Entity.entity(comment, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
        comment = response.readEntity(RestComment.class);
        Assert.assertNotNull(comment);
        log.debug(comment);
    }

    @Test(enabled = false)
    public void testAddExternalResource() {
        log.debug("*** testAddExternalResource ***");
        String id = "4803";
        RestExternalResource externalResource = new RestExternalResource();
        externalResource.setName("Simple Attachment - " + System.currentTimeMillis());
        externalResource.setDescription("Simple Attachment description");
        externalResource.setType("simpleType");
        // externalResource.setType("url");
        externalResource.setExternalUrl("http://www.google.com?q=activiti");
        Response response = target().path("runtime/tasks").path(id).path("attachments").request()
                .post(Entity.entity(externalResource, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
        externalResource = response.readEntity(RestExternalResource.class);
        Assert.assertNotNull(externalResource);
        log.debug(externalResource);
    }
}
