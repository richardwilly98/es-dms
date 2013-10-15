package com.github.richardwilly98.activiti.rest.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.activiti.rest.api.RestUrls;

import com.github.richardwilly98.activiti.rest.api.Deployment;
import com.github.richardwilly98.activiti.rest.api.SearchResult;

public class RestDeploymentServiceClient extends RestClientBase {

    private String path = RestUrls.createRelativeResourceUrl(RestUrls.URL_DEPLOYMENT_COLLECTION);
    
    public RestDeploymentServiceClient(URI url) {
        super(url);
    }

    public SearchResult<Deployment> getDeployments() {
        log.debug("*** getDeployments ***");
        SearchResult<Deployment> deploymentList = null;
        Response response = target().path(path).request().cookie(new Cookie("ES_DMS_TICKET", getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            deploymentList = response.readEntity(new GenericType<SearchResult<Deployment>>() {
            });
        }
        return deploymentList;
    }
    
    public Deployment getDeploment(String id) {
        checkNotNull(id);
        Deployment deployment = null;
        Response response = target().path(path).path(id).request().cookie(new Cookie("ES_DMS_TICKET", getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            deployment = response.readEntity(Deployment.class);
        }
        return deployment;
    }

}
