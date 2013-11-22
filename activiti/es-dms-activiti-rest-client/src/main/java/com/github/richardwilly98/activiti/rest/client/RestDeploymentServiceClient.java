package com.github.richardwilly98.activiti.rest.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.activiti.rest.service.api.RestUrls;

import com.github.richardwilly98.activiti.rest.api.RestDeployment;
import com.github.richardwilly98.activiti.rest.api.RestSearchResult;

public class RestDeploymentServiceClient extends RestClientBase<RestDeployment> {

    private static String path = RestUrls.createRelativeResourceUrl(RestUrls.URL_DEPLOYMENT_COLLECTION);
    
    public RestDeploymentServiceClient(final URI url, final int timeout) {
        super(url, timeout, path, RestDeployment.class);
    }

    public RestSearchResult<RestDeployment> getDeployments() {
        log.debug("*** getDeployments ***");
        try {
        Response response = target().path(path).request().cookie(new Cookie(ES_DMS_TICKET, getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(new GenericType<RestSearchResult<RestDeployment>>() {
            });
        }
        throw new WebApplicationException(response);
        } catch (ProcessingException pEx) {
            log.error("getDeployments failed.", pEx);
            throw new WebApplicationException(pEx);
        }
    }
    
    public RestDeployment getDeploment(String id) {
        checkNotNull(id);
        Response response = target().path(path).path(id).request().cookie(new Cookie(ES_DMS_TICKET, getToken())).get();
        if(response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(RestDeployment.class);
        }
        throw new WebApplicationException(response);
    }

}
