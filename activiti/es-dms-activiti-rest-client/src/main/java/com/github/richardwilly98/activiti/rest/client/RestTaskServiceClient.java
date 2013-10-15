package com.github.richardwilly98.activiti.rest.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.activiti.rest.api.RestUrls;

import com.github.richardwilly98.activiti.rest.api.Comment;
import com.github.richardwilly98.activiti.rest.api.ExternalResource;
import com.github.richardwilly98.activiti.rest.api.SearchResult;
import com.github.richardwilly98.activiti.rest.api.Task;
import com.github.richardwilly98.esdms.exception.ServiceException;

public class RestTaskServiceClient extends RestClientBase {

    private String path = RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_COLLECTION);

    public RestTaskServiceClient(URI url) {
        super(url);
    }

    public SearchResult<Task> getTasks() throws ServiceException {
        log.debug("*** getTasks ***");
        Response response = target().path(path).request().cookie(new Cookie("ES_DMS_TICKET", getToken())).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(new GenericType<SearchResult<Task>>() {
            });
        }
        throw new ServiceException(String.format("Fail to get tasks. Response status %s", response.getStatus()));
    }

    public Task getTask(String taskId) throws ServiceException {
        checkNotNull(taskId);
        Response response = target().path(path).path(taskId).request().cookie(new Cookie("ES_DMS_TICKET", getToken())).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(Task.class);
        }
        throw new ServiceException(String.format("Fail to get task %s. Response status %s", taskId, response.getStatus()));
    }

    public Comment addComment(String taskId, Comment comment) throws ServiceException {
        checkNotNull(taskId);
        checkNotNull(comment);
        Response response = target().path(path).path(taskId).path(RestUrls.SEGMENT_COMMENTS).request()
                .cookie(new Cookie("ES_DMS_TICKET", getToken())).post(Entity.entity(comment, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() == Status.CREATED.getStatusCode()) {
            return response.readEntity(Comment.class);
        }
        throw new ServiceException(String.format("Fail to add comment to task %s. Response status %s", taskId, response.getStatus()));
    }

    public ExternalResource addExternalResource(String taskId, ExternalResource resource) throws ServiceException {
        checkNotNull(taskId);
        checkNotNull(resource);
        Response response = target().path(path).path(taskId).path(RestUrls.SEGMENT_ATTACHMENTS).request()
                .cookie(new Cookie("ES_DMS_TICKET", getToken())).post(Entity.entity(resource, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() == Status.CREATED.getStatusCode()) {
            return response.readEntity(ExternalResource.class);
        }
        throw new ServiceException(String.format("Fail to add resource to task %s. Response status %s", taskId, response.getStatus()));
    }
}
