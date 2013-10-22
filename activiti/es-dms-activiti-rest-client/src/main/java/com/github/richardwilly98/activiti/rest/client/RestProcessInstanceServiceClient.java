package com.github.richardwilly98.activiti.rest.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.activiti.rest.api.RestUrls;

import com.github.richardwilly98.activiti.rest.api.RestProcessInstance;
import com.github.richardwilly98.activiti.rest.api.RestSearchResult;

public class RestProcessInstanceServiceClient extends RestClientBase<RestProcessInstance> {

    private static String path = RestUrls.createRelativeResourceUrl(RestUrls.URL_PROCESS_INSTANCE_COLLECTION);
    
    public RestProcessInstanceServiceClient(URI url) {
        super(url, path, RestProcessInstance.class);
    }

    public RestSearchResult<RestProcessInstance> getProcessInstances() {
        log.trace("*** getProcessInstances ***");
        Response response = target().path(path).request().cookie(new Cookie(ES_DMS_TICKET, getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            return  response.readEntity(new GenericType<RestSearchResult<RestProcessInstance>>() {
            });
        }
        throw new WebApplicationException(response);
    }
    
    public RestProcessInstance getProcessInstance(String id) {
        log.trace(String.format("*** getProcessInstance - %s ***", id));
        checkNotNull(id);
        Response response = target().path(path).path(id).request().cookie(new Cookie(ES_DMS_TICKET, getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(RestProcessInstance.class);
        }
        throw new WebApplicationException(response);
    }
    
    public RestProcessInstance startProcessInstance(String processDefinitionId) {
        log.trace(String.format("*** startProcessInstance - %s ***", processDefinitionId));
        checkNotNull(processDefinitionId);
        RestProcessInstance processInstance = new RestProcessInstance();
        processInstance.setProcessDefinitionId(processDefinitionId);
        Response response = target().path(path).request().cookie(new Cookie(ES_DMS_TICKET, getToken())).post(Entity.json(processInstance));
        if (response.getStatus() == Status.CREATED.getStatusCode()) {
            return response.readEntity(RestProcessInstance.class);
        }
        throw new WebApplicationException(response);
    }

}
