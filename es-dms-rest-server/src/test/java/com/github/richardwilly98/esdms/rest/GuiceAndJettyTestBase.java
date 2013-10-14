package com.github.richardwilly98.esdms.rest;

/*
 * #%L
 * es-dms-site
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.testng.Assert;

import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;

/*
 * TODO: Investigate why SSL does not work.
 */
//@Guice(modules = TestEsClientModule.class)
public class GuiceAndJettyTestBase<T extends ItemBase> extends TestRestServerBase {

    GuiceAndJettyTestBase() throws Exception {
        super();
    }

    protected T get(String id, Class<T> type, String path) throws Throwable {
        Response response = target().path(path).path(id).request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
        log.debug(String.format("status: %s", response.getStatus()));
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(type);
        }
        return null;
    }

    protected T get(URI uri, Class<T> type) throws Throwable {
        log.trace(String.format("getItem - %s", uri));
        Response response = client().target(uri).request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
        log.trace(String.format("status: %s", response.getStatus()));
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(type);
        }
        return null;
    }

    protected T update(ItemBase item, Class<T> type, String path) throws Throwable {
        Response response = target().path(path).path(item.getId()).request(MediaType.APPLICATION_JSON).cookie(adminCookie)
                .put(Entity.json(item));
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new ServiceException(String.format("update failed. Response status: %s", response.getStatus()));
        }
        return response.readEntity(type);
    }

    protected void delete(String id, String path) throws Throwable {
        Response response = target().path(path).path(id).request().cookie(adminCookie).delete();
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new ServiceException(String.format("delete failed. Response status: %s", response.getStatus()));
        }
    }

    protected Cookie login(Credential credential) throws Throwable {
        // try {
        log.trace("*** login ***");
        WebTarget webResource = target().path(RestAuthenticationService.AUTH_PATH).path(RestAuthenticationService.LOGIN_PATH);
        Response response = webResource.request(MediaType.APPLICATION_JSON).post(Entity.entity(credential, MediaType.APPLICATION_JSON));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        for (NewCookie cookie : response.getCookies().values()) {
            if (RestAuthenticationService.ES_DMS_TICKET.equals(cookie.getName())) {
                return new Cookie(cookie.getName(), cookie.getValue());
            }
        }
        // } catch (Throwable t) {
        // log.error("login failed", t);
        // Assert.fail("login failed", t);
        // }
        return null;
    }

    protected void logout(Cookie cookie) throws Throwable {
        log.trace("*** logout ***");
        checkNotNull(cookie);
        WebTarget webResource = target().path(RestAuthenticationService.AUTH_PATH).path(RestAuthenticationService.LOGOUT_PATH);
        Response response = webResource.request().cookie(cookie).post(Entity.json(null));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new ServiceException(String.format("logout failed. Response status: %s", response.getStatus()));
        }

    }

    protected User createUser(String login, String password) throws Throwable {
        return createUser(login, password, null);
    }

    protected User createUser(String login, String password, Set<Role> roles) throws Throwable {
        log.debug(String.format("*** createUser - %s - %s ***", login, password));
        User user = new UserImpl.Builder().id(login).name(login).email(login).login(login).password(password.toCharArray()).roles(roles)
                .build();
        String json = mapper.writeValueAsString(user);
        log.trace(json);
        Response response = target().path(RestUserService.USERS_PATH).request(MediaType.APPLICATION_JSON).cookie(adminCookie)
                .post(Entity.json(user));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            throw new ServiceException(String.format("createUser %s failed. Response status: %s", login, response.getStatus()));
        }
        URI uri = response.getLocation();
        Assert.assertNotNull(uri);
        log.debug(String.format("getItem - %s", uri));
        response = client().target(uri).request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(User.class);
        }
        log.warn(String.format("status: %s", response.getStatus()));
        return null;
    }

}
