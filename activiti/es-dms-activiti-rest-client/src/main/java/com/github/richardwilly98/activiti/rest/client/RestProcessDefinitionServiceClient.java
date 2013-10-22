package com.github.richardwilly98.activiti.rest.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.activiti.rest.api.RestUrls;

import com.github.richardwilly98.activiti.rest.api.RestProcessDefinition;
import com.github.richardwilly98.activiti.rest.api.RestSearchResult;

public class RestProcessDefinitionServiceClient extends RestClientBase<RestProcessDefinition> {

    private static String path = RestUrls.createRelativeResourceUrl(RestUrls.URL_PROCESS_DEFINITION_COLLECTION);
    
    public RestProcessDefinitionServiceClient(URI url) {
        super(url, path, RestProcessDefinition.class);
    }

    public RestSearchResult<RestProcessDefinition> getProcessDefinitions() {
        log.debug("*** getProcessDefinitions ***");
        RestSearchResult<RestProcessDefinition> processDefinitionList = null;
        Response response = target().path(path).request().cookie(new Cookie(ES_DMS_TICKET, getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            processDefinitionList = response.readEntity(new GenericType<RestSearchResult<RestProcessDefinition>>() {
            });
        }
        return processDefinitionList;
    }
    
    public RestSearchResult<RestProcessDefinition> getProcessDefinitionsByCategory(String category) {
        log.debug("*** getProcessDefinitionsByDeplomentId ***");
        RestSearchResult<RestProcessDefinition> processDefinitionList = null;
        Response response = target().path(path).queryParam("category", category).request().cookie(new Cookie(ES_DMS_TICKET, getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            processDefinitionList = response.readEntity(new GenericType<RestSearchResult<RestProcessDefinition>>() {
            });
        }
        return processDefinitionList;
    }

    public RestSearchResult<RestProcessDefinition> getProcessDefinitionsByDeplomentId(String deploymentId) {
        log.debug("*** getProcessDefinitionsByDeplomentId ***");
        RestSearchResult<RestProcessDefinition> processDefinitionList = null;
        Response response = target().path(path).queryParam("deploymentId", deploymentId).request().cookie(new Cookie(ES_DMS_TICKET, getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            processDefinitionList = response.readEntity(new GenericType<RestSearchResult<RestProcessDefinition>>() {
            });
        }
        return processDefinitionList;
    }

    public RestProcessDefinition getProcessDefinition(String id) {
        checkNotNull(id);
        RestProcessDefinition processDefinition = null;
        Response response = target().path(path).path(id).request().cookie(new Cookie(ES_DMS_TICKET, getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            processDefinition = response.readEntity(RestProcessDefinition.class);
        }
        return processDefinition;
    }

}
