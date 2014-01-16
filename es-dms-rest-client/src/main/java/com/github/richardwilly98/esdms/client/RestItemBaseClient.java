package com.github.richardwilly98.esdms.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.exception.ServiceException;

public abstract class RestItemBaseClient<T extends ItemBase> extends RestClientBase {

    private final Class<T> clazz;

    protected RestItemBaseClient(final String url, final String rootResource, final Class<T> clazz) {
        super(url, rootResource);
        checkNotNull(clazz);
        this.clazz = clazz;
    }

    public T create(String token, T item) throws ServiceException {
        checkNotNull(token);
        checkNotNull(item);
        MultivaluedMap<String,Object> header = getAuthenticationHeader(token);
        Response response = target().request(MediaType.APPLICATION_JSON).headers(header)
                .post(Entity.json(item));
        log.debug(String.format("status: %s", response.getStatus()));
        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            throw new ServiceException(String.format("Fail to create user. Response stauts: %s", response.getStatus()));
        }
        URI uri = response.getLocation();
        log.debug(String.format("getItem - %s", uri));
        response = restClient.target(uri).request().headers(header).accept(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(clazz);
        }
        return null;
    }

    public void delete(String token, T item) throws ServiceException {
        checkNotNull(token);
        checkNotNull(item);
        MultivaluedMap<String,Object> header = getAuthenticationHeader(token);
        Response response = target().path(item.getId()).request(MediaType.APPLICATION_JSON).headers(header)
                .delete();
        log.debug(String.format("status: %s", response.getStatus()));
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new ServiceException(String.format("Fail to delete user %s. Response status: %s", item.getId(), response.getStatus()));
        }
    }

}
