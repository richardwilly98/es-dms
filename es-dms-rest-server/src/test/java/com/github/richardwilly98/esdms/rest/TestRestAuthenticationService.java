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

import java.net.URI;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.RestAuthenticationService.ItemResponse;

public class TestRestAuthenticationService extends TestRestUserService {

    public TestRestAuthenticationService() throws Exception {
        super();
    }

    @Test
    public void testLoginLogout() throws Throwable {
        log.debug("*** testLoginLogout ***");
        try {
            String password = "secret1";
            String login = "user-" + System.currentTimeMillis() + "@yahoo.com";
            createUser(login, password);
            boolean rememberMe = true;
            Credential credential = new CredentialImpl.Builder().username(login).password(password.toCharArray()).rememberMe(rememberMe)
                    .build();
            MultivaluedMap<String,Object> header = login(credential);
            Assert.assertNotNull(header);
            logout(header);
        } catch (Throwable t) {
            log.error("testLoginLogout fail", t);
            Assert.fail();
        }
    }

    @Test
    public void testLoginValidate() throws Throwable {
        try {
            String id = "user-" + System.currentTimeMillis();
            String login = id + "@yahoo.com";
            User user = createUser(id, login, login, null);
            log.debug(String.format("User created: %s", user));
            boolean rememberMe = true;
            Credential credential = new CredentialImpl.Builder().username(login).password(login.toCharArray()).rememberMe(rememberMe)
                    .build();
            MultivaluedMap<String,Object> header = login(credential);
            Assert.assertNotNull(header);
            validate(header);
            logout(header);
        } catch (Throwable t) {
            log.error("testLoginValidate fail", t);
            Assert.fail();
        }
    }

    private void validate(MultivaluedMap<String, Object> headers) throws Throwable {
        log.trace("*** validate ***");
        Assert.assertNotNull(headers);
        WebTarget webResource = target().path(RestAuthenticationService.AUTH_PATH).path(RestAuthenticationService.VALIDATE_PATH);
        Response response = webResource.request(MediaType.APPLICATION_JSON).headers(headers).post(Entity.json(null));
        Assert.assertEquals(response.getStatus(), Status.OK.getStatusCode());
        ItemResponse itemResponse = response.readEntity(ItemResponse.class);
        Assert.assertNotNull(itemResponse);
    }

    private User createUser(String id, String login, String password, Set<Role> roles) throws Throwable {
        log.debug(String.format("*** createUser - %s - %s - %s ***", id, login, password));
        User user = new UserImpl.Builder().id(id).name(login).email(login).login(login).password(password.toCharArray()).roles(roles)
                .build();
        String json = mapper.writeValueAsString(user);
        log.debug("About to create user: " + json);
        Response response = target().path(RestUserService.USERS_PATH).request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader)
                .post(Entity.json(user));
        Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            throw new ServiceException(String.format("createUser %s failed. Response status: %s", login, response.getStatus()));
        }
        URI uri = response.getLocation();
        Assert.assertNotNull(uri);
        log.debug(String.format("getItem - %s", uri));
        response = client().target(uri).request().headers(adminAuthenticationHeader).accept(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(User.class);
        }
        log.warn(String.format("status: %s", response.getStatus()));
        return null;
    }
}
