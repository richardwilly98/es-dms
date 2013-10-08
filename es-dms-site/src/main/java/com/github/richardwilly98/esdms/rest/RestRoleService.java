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

import static com.google.common.collect.Sets.newHashSet;

import java.util.AbstractMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.RoleService;

@Path(RestRoleService.ROLES_PATH)
public class RestRoleService extends RestItemBaseService<Role> {

    public static final String TYPES_PATH = "_types";
    public static final String ROLES_PATH = "roles";

    @Inject
    public RestRoleService(AuthenticationService authenticationService, final RoleService roleService) {
        super(authenticationService, roleService);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path(TYPES_PATH)
    public Response getRoleTypes() {
        if (log.isTraceEnabled()) {
            log.trace("getRoleTypes");
        }
        Set<Map.Entry<Integer, String>> types = newHashSet();
        for (Role.RoleType type : EnumSet.allOf(Role.RoleType.class)) {
            types.add(new AbstractMap.SimpleEntry<Integer, String>(type.getType(), type.getDescription()));
        }
        return Response.status(Status.OK).entity(types).build();
    }

}
