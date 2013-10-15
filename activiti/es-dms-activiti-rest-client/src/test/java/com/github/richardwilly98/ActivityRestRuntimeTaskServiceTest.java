package com.github.richardwilly98;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import com.github.richardwilly98.activiti.rest.api.Comment;
import com.github.richardwilly98.activiti.rest.api.ExternalResource;
import com.github.richardwilly98.activiti.rest.api.SearchResult;
import com.github.richardwilly98.activiti.rest.api.Task;

public class ActivityRestRuntimeTaskServiceTest extends ActivitiRestClientTest {

    @Test(enabled = false)
    public void testCleanUpTasks() {
        log.debug("*** testCleanUpTasks ***");
        Response response = target().path("runtime/tasks").queryParam("size", 100).request().get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        SearchResult<Task> taskList = response.readEntity(new GenericType<SearchResult<Task>>() {
        });
        Assert.assertNotNull(taskList);
        for (Task task : taskList.getData()) {
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
        SearchResult<Task> taskList = response.readEntity(new GenericType<SearchResult<Task>>() {
        });
        Assert.assertNotNull(taskList);
        for (Task task : taskList.getData()) {
            log.debug(task);
        }
    }

    @Test(enabled = false)
    public void testNewTasks() {
        log.debug("*** testNewTasks ***");
        Response response = target().path("runtime/tasks").request().get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        SearchResult<Task> taskList = response.readEntity(new GenericType<SearchResult<Task>>() {
        });
        Assert.assertNotNull(taskList);
        for (Task task : taskList.getData()) {
            log.debug(task);
        }
    }

    @Test(enabled = false)
    public void testAddComment() {
        log.debug("*** testAddComment ***");
        Response response = target().path("runtime/tasks").request().get();
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        SearchResult<Task> taskList = response.readEntity(new GenericType<SearchResult<Task>>() {
        });
        Assert.assertNotNull(taskList);
        String id = null;
        for (Task task : taskList.getData()) {
            log.debug(task);
            id = task.getId();
            break;
        }
        id = "2340";
        Assert.assertNotNull(id);
        Comment comment = new Comment();
        comment.setAuthor(ADMIN_USERNAME);
        comment.setMessage("This is my first comment");
        response = target().path("runtime/tasks").path(id).path("comments").request()
                .post(Entity.entity(comment, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
        comment = response.readEntity(Comment.class);
        Assert.assertNotNull(comment);
        log.debug(comment);
    }

    @Test(enabled = false)
    public void testAddExternalResource() {
        log.debug("*** testAddExternalResource ***");
        String id = "4803";
        ExternalResource externalResource = new ExternalResource();
        externalResource.setName("Simple Attachment - " + System.currentTimeMillis());
        externalResource.setDescription("Simple Attachment description");
        externalResource.setType("simpleType");
        // externalResource.setType("url");
        externalResource.setExternalUrl("http://www.google.com?q=activiti");
        Response response = target().path("runtime/tasks").path(id).path("attachments").request()
                .post(Entity.entity(externalResource, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
        externalResource = response.readEntity(ExternalResource.class);
        Assert.assertNotNull(externalResource);
        log.debug(externalResource);
    }
}
