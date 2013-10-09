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

import java.util.EnumSet;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.rest.RestRoleService;
import com.github.richardwilly98.esdms.services.RoleService;

public class TestRestRoleService extends GuiceAndJettyTestBase<UserImpl> {

    public TestRestRoleService() throws Exception {
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

    static class SimpleEntry {
        private int key;
        private String value;

        int getKey() {
            return key;
        }

        void setKey(int key) {
            this.key = key;
        }

        String getValue() {
            return value;
        }

        void setValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void testFindRoleByTypes() throws Throwable {
        log.debug("*** testFindRoleByTypes ***");
        try {
            Response response;
            response = target().path(RestRoleService.ROLES_PATH).queryParam("type", RoleType.SYSTEM.getType()).request(MediaType.APPLICATION_JSON)
                    .cookie(adminCookie).get();
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            SearchResult<Role> roles = response.readEntity(new GenericType<SearchResult<Role>>() {
            });
            Assert.assertNotNull(roles);
            Assert.assertTrue(roles.getTotalHits() > 0);
            for (Role role : roles.getItems()) {
                log.trace(role);
                Assert.assertNotNull(role);
                Assert.assertEquals(role.getType(), RoleType.SYSTEM);
            }
        } catch (Throwable t) {
            log.error("testFindRoleByTypes fail", t);
            Assert.fail();
        }
    }
    
    @Test
    public void testLoadDefaultRoles() throws Throwable {
        log.debug("*** testLoadDefaultRoles ***");
        try {
            for(RoleService.DefaultRoles defaultRole : EnumSet.allOf(RoleService.DefaultRoles.class)) {
                Assert.assertNotNull(getRole(defaultRole.getRole().getId()));
            }
        } catch (Throwable t) {
            log.error("testLoadSystemRoles fail", t);
            Assert.fail();
        }
    }
    
    // TODO: Validate system role cannot be updated or deleted.

}
