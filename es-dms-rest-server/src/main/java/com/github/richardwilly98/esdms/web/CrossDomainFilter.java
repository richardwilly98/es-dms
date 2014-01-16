package com.github.richardwilly98.esdms.web;

/*
 * #%L
 * es-dms-rest-server
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

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.inject.SystemParametersModule;

@Provider
public class CrossDomainFilter implements ContainerResponseFilter {

    public long sessionTimeout = SystemParametersModule.DEFAULT_SESSION_TIMEOUT;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String origin = requestContext.getHeaderString("Origin");
        responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
        // responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        String headers = "origin, content-type, accept, authorization, " + User.ES_DMS_TICKET.toLowerCase();
        responseContext.getHeaders().add("Access-Control-Allow-Headers", headers);
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        // responseContext.getHeaders().add("Access-Control-Max-Age",
        // "1209600");
        responseContext.getHeaders().add("Access-Control-Max-Age", sessionTimeout / 1000);
    }

}
