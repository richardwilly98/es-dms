package com.github.richardwilly98.activiti.rest.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.github.richardwilly98.activiti.rest.api.RestItemBase;
import com.github.richardwilly98.esdms.exception.ServiceException;

public abstract class RestClientBase<T extends RestItemBase> {

    public static final String ES_DMS_TICKET = "ES_DMS_TICKET";
    protected final Logger log = Logger.getLogger(getClass());
    private final Client restClient;

    // Url should include activiti-rest/service
    private final URI url;
    private final String path;
    private final Class<T> clazz;
    private String token;

    public RestClientBase(final URI url, final String path, final Class<T> clazz) {
        this.url = url;
        this.path = path;
        this.clazz = clazz;
        ClientConfig configuration = new ClientConfig();
        configuration.register(MultiPartFeature.class);
        restClient = ClientBuilder.newClient(configuration);
    }

    protected URI getBaseURI() {
        return UriBuilder.fromUri(url).build();
    }

    protected WebTarget target() {
        return target(null);
    }

    /**
     * Create a web resource whose URI refers to the base URI the Web
     * application is deployed at.
     * 
     * @return the created web resource
     */
    protected WebTarget target(String path) {
        WebTarget target = restClient.target(getBaseURI());
        // if (!Strings.isNullOrEmpty(path)) {
        // target.path(path);
        // }
        // if (!Strings.isNullOrEmpty(token)) {
        // target.request().cookie(new Cookie("ES_DMS_TICKET", token));
        // }
        return target;
    }

    public T create(T item) throws ServiceException {
        checkNotNull(item);
        String id = item.getId();
        Response response = target().path(path).path(id).request().cookie(new Cookie(ES_DMS_TICKET, getToken()))
                .post(Entity.json(convertItemToRequest(item)));
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(clazz);
        }
        throw new ServiceException(String.format("Fail to create item %s. Response status %s", id, response.getStatus()));
    }

    protected Object convertItemToRequest(T item) {
        checkNotNull(item);
        return item;
    }

    public T update(T item) throws ServiceException {
        checkNotNull(item);
        String id = item.getId();
        Response response = target().path(path).path(id).request().cookie(new Cookie(ES_DMS_TICKET, getToken()))
                .put(Entity.json(convertItemToRequest(item)));
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(clazz);
        }
        throw new ServiceException(String.format("Fail to update item %s. Response status %s", id, response.getStatus()));
    }

    public void setCredentials(String username, byte[] password) {
        restClient.register(new HttpBasicAuthFilter(username, password));
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
