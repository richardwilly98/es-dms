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

import java.util.EnumSet;
import java.util.Set;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.rest.RestRoleTypeService;

public class TestRestRoleTypeService extends GuiceAndJettyTestBase<Role> {

    public TestRestRoleTypeService() throws Exception {
        super();
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
    public void testGetRoleTypes() throws Throwable {
        log.debug("*** testGetRoleTypes ***");
        try {
            Response response;
            response = target().path(RestRoleTypeService.ROLE_TYPES_PATH).request(MediaType.APPLICATION_JSON).cookie(adminCookie).get();
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            Set<SimpleEntry> roleTypes = response.readEntity(new GenericType<Set<SimpleEntry>>() {
            });
            Assert.assertNotNull(roleTypes);
            Assert.assertEquals(roleTypes.size(), EnumSet.allOf(RoleType.class).size());
            for (SimpleEntry entry : roleTypes) {
                RoleType type = RoleType.fromValue(entry.getKey());
                Assert.assertNotNull(type);
            }
        } catch (Throwable t) {
            log.error("testGetRoleTypes failed", t);
            Assert.fail();
        }
    }

}
