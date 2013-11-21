package com.github.richardwilly98.activiti.rest.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.activiti.rest.service.api.RestUrls;
import org.activiti.rest.service.api.runtime.task.TaskRequest;

import com.github.richardwilly98.activiti.rest.api.RestComment;
import com.github.richardwilly98.activiti.rest.api.RestExternalResource;
import com.github.richardwilly98.activiti.rest.api.RestSearchResult;
import com.github.richardwilly98.activiti.rest.api.RestTask;
import com.github.richardwilly98.esdms.exception.ServiceException;

public class RestTaskServiceClient extends RestClientBase<RestTask> {

//    @Override
//    public RestTask update(RestTask item) throws ServiceException {
//        item.setCreateTime(null);
//        item.setTaskDefinitionKey(null);
//        item.setExecutionId(null);
//        item.setExecutionUrl(null);
//        item.setProcessInstanceId(null);
//        item.setProcessInstanceUrl(null);
//        item.setProcessDefinitionId(null);
//        item.setProcessDefinitionUrl(null);
//        item.setVariables(null);
//        return super.update(item);
//    }

    private static String path = RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_COLLECTION);

    public RestTaskServiceClient(URI url) {
        super(url, path, RestTask.class);
    }

    @Override
    protected Object convertItemToRequest(RestTask item) {
        checkNotNull(item);
        TaskRequest request = new TaskRequest();
        request.setAssignee(item.getAssignee());
//        request.setDelegationState();
        request.setDescription(item.getDescription());
        request.setDueDate(item.getDueDate());
        request.setName(item.getName());
        request.setOwner(item.getOwner());
        request.setParentTaskId(item.getParentTaskId());
        request.setPriority(item.getPriority());
        return request;
    }

    public RestSearchResult<RestTask> getTasks() throws ServiceException {
        log.debug("*** getTasks ***");
        Response response = target().path(path).request().cookie(new Cookie(ES_DMS_TICKET, getToken())).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(new GenericType<RestSearchResult<RestTask>>() {
            });
        }
        throw new ServiceException(String.format("Fail to get tasks. Response status %s", response.getStatus()));
    }

    public RestSearchResult<RestTask> getTasksByProcessInstance(String instanceId) throws ServiceException {
        log.debug(String.format("*** getTasksByProcessInstance - %s ***", instanceId));
        Response response = target().path(path).queryParam("processInstanceId", instanceId).request()
                .cookie(new Cookie(ES_DMS_TICKET, getToken())).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(new GenericType<RestSearchResult<RestTask>>() {
            });
        }
        throw new ServiceException(String.format("Fail to get tasks. Response status %s", response.getStatus()));
    }

    public RestTask getTask(String taskId) throws ServiceException {
        checkNotNull(taskId);
        Response response = target().path(path).path(taskId).request().cookie(new Cookie(ES_DMS_TICKET, getToken())).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(RestTask.class);
        }
        throw new ServiceException(String.format("Fail to get task %s. Response status %s", taskId, response.getStatus()));
    }

    public RestComment addComment(String taskId, RestComment comment) throws ServiceException {
        checkNotNull(taskId);
        checkNotNull(comment);
        Response response = target().path(path).path(taskId).path(RestUrls.SEGMENT_COMMENTS).request()
                .cookie(new Cookie(ES_DMS_TICKET, getToken())).post(Entity.entity(comment, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() == Status.CREATED.getStatusCode()) {
            return response.readEntity(RestComment.class);
        }
        throw new ServiceException(String.format("Fail to add comment to task %s. Response status %s", taskId, response.getStatus()));
    }

    public RestExternalResource addExternalResource(String taskId, RestExternalResource resource) throws ServiceException {
        checkNotNull(taskId);
        checkNotNull(resource);
        Response response = target().path(path).path(taskId).path(RestUrls.SEGMENT_ATTACHMENTS).request()
                .cookie(new Cookie(ES_DMS_TICKET, getToken())).post(Entity.entity(resource, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() == Status.CREATED.getStatusCode()) {
            return response.readEntity(RestExternalResource.class);
        }
        throw new ServiceException(String.format("Fail to add resource to task %s. Response status %s", taskId, response.getStatus()));
    }

}
