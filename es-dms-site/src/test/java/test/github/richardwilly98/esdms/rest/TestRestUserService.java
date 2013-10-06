package test.github.richardwilly98.esdms.rest;

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

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.rest.RestItemBaseService;
import com.github.richardwilly98.esdms.rest.RestRoleService;
import com.github.richardwilly98.esdms.rest.RestUserService;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.common.collect.ImmutableSet;

//public class TestRestAuthenticationService extends GuiceAndJerseyTestBase {
public class TestRestUserService extends GuiceAndJettyTestBase<UserImpl> {

    public TestRestUserService() throws Exception {
	super();
    }

    private Role getRole(String id) throws Throwable {
	Response response = target().path(RestRoleService.ROLES_PATH).path(id).request(MediaType.APPLICATION_JSON).cookie(adminCookie)
	        .accept(MediaType.APPLICATION_JSON).get();
	log.debug(String.format("status: %s", response.getStatus()));
	if (response.getStatus() == Status.OK.getStatusCode()) {
	    return response.readEntity(Role.class);
	}
	return null;
    }

    @Test
    public void testGetUsers() throws Throwable {
	log.debug("*** testGetUsers ***");
	try {
	    Response response;
	    log.debug("Resource: " + target());
	    response = target().path(RestUserService.USERS_PATH).path(RestItemBaseService.SEARCH_PATH)
		    .path(UserService.DEFAULT_ADMIN_LOGIN).request(MediaType.APPLICATION_JSON).cookie(adminCookie).get();
	    log.debug("status: " + response.getStatus());
	    Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
	    SearchResult<User> users = response.readEntity(new GenericType<SearchResult<User>>() {
	    });
	    Assert.assertNotNull(users);
	    Assert.assertTrue(users.getTotalHits() >= 1);
	} catch (Throwable t) {
	    log.error("testGetUsers fail", t);
	    Assert.fail();
	}
    }

    @Test
    public void testCreateRetrieveDeleteUpdate() throws Throwable {
	log.debug("*** testCreateRetrieveDeleteUpdate ***");
	try {
	    String password = "secret1";
	    String login = "user-" + System.currentTimeMillis();
	    User user1 = createUser(login, password, ImmutableSet.of(RoleService.DefaultRoles.WRITER.getRole()));
	    Assert.assertNotNull(user1);
	    Assert.assertNotNull(user1.getRoles());
	    Role writerRole = getRole(RoleService.DefaultRoles.WRITER.getRole().getId());
	    Assert.assertTrue(user1.getRoles().contains(writerRole));
	    UserImpl user2 = get(user1.getId(), UserImpl.class, RestUserService.USERS_PATH);
	    Assert.assertEquals(user1.getName(), user2.getName());
	    String newName = "user-2-" + System.currentTimeMillis();
	    user2.setName(newName);
	    UserImpl user3 = update(user2, UserImpl.class, RestUserService.USERS_PATH);
	    Assert.assertEquals(newName, user3.getName());
	    delete(user1.getId(), RestUserService.USERS_PATH);
	    user2 = get(user1.getId(), UserImpl.class, RestUserService.USERS_PATH);
	    Assert.assertNull(user2);
	} catch (Throwable t) {
	    log.error("testCreateRetrieveDeleteUpdate fail", t);
	    Assert.fail();
	}
    }

}
