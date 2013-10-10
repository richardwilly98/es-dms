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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.services.UserService;

public class SimpleRestServiceTest extends TestRestServerBase {

    public SimpleRestServiceTest() throws Exception {
        super();
    }

    @Test
    public void testLoginAdmin() throws Throwable {
        log.debug("*** testLoginAdmin ***");
        try {
            Credential credential = new CredentialImpl.Builder().username(UserService.DEFAULT_ADMIN_LOGIN).password(UserService.DEFAULT_ADMIN_PASSWORD.toCharArray()).build();
            Response response = target().path(RestAuthencationService.AUTH_PATH).path(RestAuthencationService.LOGIN_PATH).request(MediaType.APPLICATION_JSON).post(Entity.entity(credential, MediaType.APPLICATION_JSON));
            Assert.assertEquals(response.getStatus(), Status.OK.getStatusCode());
        } catch (Throwable t) {
            log.error("testLoginAdmin fail", t);
            Assert.fail();
        }
    }

}
