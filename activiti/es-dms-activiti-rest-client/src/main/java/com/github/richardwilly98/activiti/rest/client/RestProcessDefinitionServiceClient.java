package com.github.richardwilly98.activiti.rest.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.activiti.rest.api.RestUrls;

import com.github.richardwilly98.activiti.rest.api.ProcessDefinition;
import com.github.richardwilly98.activiti.rest.api.SearchResult;

public class RestProcessDefinitionServiceClient extends RestClientBase {

    private String path = RestUrls.createRelativeResourceUrl(RestUrls.URL_PROCESS_DEFINITION_COLLECTION);
    
    public RestProcessDefinitionServiceClient(URI url) {
        super(url);
    }

    public SearchResult<ProcessDefinition> getProcessDefinitions() {
        log.debug("*** getProcessDefinitions ***");
        SearchResult<ProcessDefinition> processDefinitionList = null;
        Response response = target().path(path).request().cookie(new Cookie("ES_DMS_TICKET", getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            processDefinitionList = response.readEntity(new GenericType<SearchResult<ProcessDefinition>>() {
            });
        }
        return processDefinitionList;
    }
    
    public ProcessDefinition getProcessDefinition(String id) {
        checkNotNull(id);
        ProcessDefinition processDefinition = null;
        Response response = target().path(path).path(id).request().cookie(new Cookie("ES_DMS_TICKET", getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            processDefinition = response.readEntity(ProcessDefinition.class);
        }
        return processDefinition;
    }

}
