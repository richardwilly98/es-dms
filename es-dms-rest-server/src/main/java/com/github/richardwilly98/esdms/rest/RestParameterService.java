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

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.richardwilly98.esdms.api.Parameter;
import com.github.richardwilly98.esdms.api.Parameter.ParameterType;
import com.github.richardwilly98.esdms.search.api.SearchResult;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.exception.RestServiceException;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.ParameterService;

@Path(RestParameterService.PARAMETERS_PATH)
public class RestParameterService extends RestItemBaseService<Parameter> {

    public static final String PARAMETERS_PATH = "parameters";
    private final ParameterService parameterService;

    @Inject
    public RestParameterService(AuthenticationService authenticationService, final ParameterService parameterService) {
        super(authenticationService, parameterService);
        this.parameterService = parameterService;
    }

    /*
     * This method should execute different searches based on query parameter
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response find(@QueryParam(SEARCH_FIRST_PARAMETER) @DefaultValue("0") int first,
            @QueryParam(SEARCH_PAGE_SIZE_PARAMETER) @DefaultValue("20") int pageSize) {
        String type = url.getQueryParameters().getFirst("type");
        if (log.isTraceEnabled()) {
            log.trace(String.format("find - %s", type));
        }
        try {
            ParameterType parameterType = ParameterType.fromValue(Integer.valueOf(type));
            SearchResult<Parameter> items = parameterService.findByType(parameterType, first, pageSize);
            return Response.ok(items).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

}
