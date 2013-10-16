package com.github.richardwilly98.activiti.rest.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.activiti.rest.api.RestUrls;

import com.github.richardwilly98.activiti.rest.api.RestDeployment;
import com.github.richardwilly98.activiti.rest.api.RestSearchResult;

public class RestDeploymentServiceClient extends RestClientBase {

    private String path = RestUrls.createRelativeResourceUrl(RestUrls.URL_DEPLOYMENT_COLLECTION);
    
    public RestDeploymentServiceClient(URI url) {
        super(url);
    }

    public RestSearchResult<RestDeployment> getDeployments() {
        log.debug("*** getDeployments ***");
        RestSearchResult<RestDeployment> deploymentList = null;
        Response response = target().path(path).request().cookie(new Cookie("ES_DMS_TICKET", getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            deploymentList = response.readEntity(new GenericType<RestSearchResult<RestDeployment>>() {
            });
        }
        return deploymentList;
    }
    
    public RestDeployment getDeploment(String id) {
        checkNotNull(id);
        RestDeployment deployment = null;
        Response response = target().path(path).path(id).request().cookie(new Cookie("ES_DMS_TICKET", getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            deployment = response.readEntity(RestDeployment.class);
        }
        return deployment;
    }

}
