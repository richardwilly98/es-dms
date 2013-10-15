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

import com.github.richardwilly98.activiti.rest.api.ProcessInstance;
import com.github.richardwilly98.activiti.rest.api.SearchResult;

public class RestProcessInstanceServiceClient extends RestClientBase {

    private String path = RestUrls.createRelativeResourceUrl(RestUrls.URL_PROCESS_INSTANCE_COLLECTION);
    
    public RestProcessInstanceServiceClient(URI url) {
        super(url);
    }

    public SearchResult<ProcessInstance> getProcessInstances() {
        log.debug("*** getProcessInstances ***");
        SearchResult<ProcessInstance> processInstances = null;
        Response response = target().path(path).request().cookie(new Cookie("ES_DMS_TICKET", getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            processInstances = response.readEntity(new GenericType<SearchResult<ProcessInstance>>() {
            });
        }
        return processInstances;
    }
    
    public ProcessInstance getProcessInstance(String id) {
        checkNotNull(id);
        ProcessInstance processInstance = null;
        Response response = target().path(path).path(id).request().cookie(new Cookie("ES_DMS_TICKET", getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            processInstance = response.readEntity(ProcessInstance.class);
        }
        return processInstance;
    }
    
    public ProcessInstance startProcessInstance(String processDefinitionId) {
        checkNotNull(processDefinitionId);
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setProcessDefinitionId(processDefinitionId);
        Response response = target().path(path).request().cookie(new Cookie("ES_DMS_TICKET", getToken())).post(Entity.entity(processInstance, MediaType.APPLICATION_JSON));
        return response.readEntity(ProcessInstance.class);
    }


}
