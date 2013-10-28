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
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.search.api.Facet;
import com.github.richardwilly98.esdms.search.api.SearchResult;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.entity.FacetedQuery;
import com.github.richardwilly98.esdms.rest.exception.RestServiceException;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.SearchService;

@Path(RestSearchService.SEARCH_PATH)
public class RestSearchService extends RestServiceBase {

    public static final String SEARCH_PATH = "search";
    public static final String TAGS_PATH = "tags";
    public static final String SEARCH_ACTION = "_search";
    public static final String FACET_SEARCH_ACTION = "_facet_search";
    public static final String MORE_LIKE_THIS_ACTION = "_more_like_this";
    public static final String SUGGEST_ACTION = "_suggest";
    public static final String SEARCH_FIRST_PARAMETER = "fi";
    public static final String SEARCH_PAGE_SIZE_PARAMETER = "ps";
    public static final String SEARCH_FACET_PARAMETER = "fa";
    public static final String MORE_LIKE_THIS_MIN_TERM_FREQUENCY_PARAMETER = "mt";
    public static final String MORE_LIKE_THIS_MAX_ITEMS_PARAMETER = "mi";
    public static final String TAGS_SUGGEST_SIZE_PARAMETER = "si";

    protected final Logger log = Logger.getLogger(getClass());
    protected final SearchService<Document> service;

    @Context
    UriInfo url;

    @Inject
    public RestSearchService(final AuthenticationService authenticationService, final SearchService<Document> searchService) {
        super(authenticationService);
        this.service = searchService;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path(SEARCH_ACTION + "/{criteria}")
    public Response search(@PathParam("criteria") String criteria, @QueryParam(SEARCH_FIRST_PARAMETER) @DefaultValue("0") int first,
            @QueryParam(SEARCH_PAGE_SIZE_PARAMETER) @DefaultValue("20") int pageSize) {
        isAuthenticated();
        if (log.isTraceEnabled()) {
            log.trace(String.format("search - %s", criteria));
        }
        try {
            SearchResult<Document> items = service.search(criteria, first, pageSize);
            return Response.ok(items).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_JSON })
    @Path(FACET_SEARCH_ACTION + "/{criteria}")
    public Response facetedSearch(@PathParam("criteria") String criteria, @QueryParam(SEARCH_FIRST_PARAMETER) @DefaultValue("0") int first,
            @QueryParam(SEARCH_PAGE_SIZE_PARAMETER) @DefaultValue("20") int pageSize, FacetedQuery query) {
        isAuthenticated();
        if (log.isTraceEnabled()) {
            log.trace(String.format("facetedSearch - %s", criteria));
        }

        try {
            SearchResult<Document> items;
            if (query == null) {
                items = service.search(criteria, first, pageSize);
            } else {
                items = service.search(criteria, first, pageSize, query.getFacets(), query.getFilters());
            }
            return Response.ok(items).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path(MORE_LIKE_THIS_ACTION + "/{criteria}")
    public Response moreLikeThis(@PathParam("criteria") String criteria, @QueryParam(SEARCH_FIRST_PARAMETER) @DefaultValue("0") int first,
            @QueryParam(SEARCH_PAGE_SIZE_PARAMETER) @DefaultValue("5") int pageSize,
            @QueryParam(MORE_LIKE_THIS_MIN_TERM_FREQUENCY_PARAMETER) @DefaultValue("1") int minTermFrequency,
            @QueryParam(MORE_LIKE_THIS_MAX_ITEMS_PARAMETER) @DefaultValue("10") int maxItems) {
        isAuthenticated();
        if (log.isTraceEnabled()) {
            log.trace(String.format("moreLikeThis - %s", criteria));
        }
        try {
            SearchResult<Document> items = service.moreLikeThis(criteria, first, pageSize, minTermFrequency, maxItems);
            return Response.ok(items).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_JSON })
    @Path(TAGS_PATH + "/" + SUGGEST_ACTION + "/{criteria}")
    public Response suggestTags(@PathParam("criteria") String criteria,
            @QueryParam(TAGS_SUGGEST_SIZE_PARAMETER) @DefaultValue("10") int size) {
        isAuthenticated();
        if (log.isTraceEnabled()) {
            log.trace(String.format("suggestTags - %s - %s", criteria, size));
        }

        try {
            Facet facet = service.suggestTags(criteria, size);
            return Response.ok(facet).build();
        } catch (ServiceException e) {
            throw new RestServiceException(e.getLocalizedMessage());
        }
    }

}
