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

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.exception.RestServiceException;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.BaseService;
//import com.google.inject.Inject;

/*
 * CRUD methods MUST follow http response status code from http://www.restapitutorial.com/lessons/httpmethods.html
 */
public abstract class RestItemBaseService<T extends ItemBase> extends RestServiceBase {

	public static final String SEARCH_PATH = "search";
	public static final String SEARCH_FIRST_PARAMETER = "fi";
	public static final String SEARCH_PAGE_SIZE_PARAMETER = "ps";

	protected final Logger log = Logger.getLogger(getClass());
//	protected final AuthenticationService authenticationService;
	protected final BaseService<T> service;

	@Context
	UriInfo url;

	@Inject
	public RestItemBaseService(final AuthenticationService authenticationService,
			final BaseService<T> service) {
		super(authenticationService);
//		this.authenticationService = authenticationService;
		this.service = service;
	}

//	private String currentUser;

//	protected String isAuthenticated() {
//		try {
//			log.debug("*** isAuthenticated ***");
//			Subject currentSubject = SecurityUtils.getSubject();
//			log.debug("currentSubject.isAuthenticated(): "
//					+ currentSubject.isAuthenticated());
//			log.debug("Principal: " + currentSubject.getPrincipal());
//			if (currentSubject.getPrincipal() == null) {
//				throw new UnauthorizedException("Unauthorize request",
//						url.getPath());
//			} else {
////				if (currentUser == null) {
////					currentUser = currentSubject.getPrincipal().toString();
//				return currentSubject.getPrincipal().toString();
////				}
//			}
//		} catch (Throwable t) {
//			throw new UnauthorizedException();
//		}
//	}

//	protected String getCurrentUser() {
//		return isAuthenticated();
////		if (currentUser == null) {
////			isAuthenticated();
////		}
////		return currentUser;
//	}

	protected URI getItemUri(T item) {
		checkNotNull(item);
		return url.getBaseUriBuilder().path(getClass()).path(item.getId())
				.build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}")
	public Response get(@PathParam("id") String id) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("get - %s", id));
		}
		try {
			T item = service.get(id);
			if (item == null) {
				return Response.status(Status.NOT_FOUND).build();
			}
			return Response.ok(item).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(T item) {
		checkNotNull(item);
		if (log.isTraceEnabled()) {
			log.trace(String.format("create - %s", item));
		}
		try {
			item = service.create(item);
			return Response.created(getItemUri(item)).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{id}")
	public Response delete(@PathParam("id") String id) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("get - %s", id));
		}
		try {
			T item = service.get(id);
			if (item != null) {
				service.delete(item);
			} else {
				return Response.status(Status.NOT_FOUND).build();
			}
			return Response.ok().build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response update(@PathParam("id") String id, T item) {
		checkNotNull(item);
		if (log.isTraceEnabled()) {
			log.trace(String.format("update - %s", item));
		}
		try {
			item = service.update(item);
			return Response.ok(item).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path(SEARCH_PATH + "/{criteria}")
	public Response search(
			@PathParam("criteria") String criteria,
			@QueryParam(SEARCH_FIRST_PARAMETER) @DefaultValue("0") int first,
			@QueryParam(SEARCH_PAGE_SIZE_PARAMETER) @DefaultValue("20") int pageSize) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("search - %s", criteria));
		}
		try {
			SearchResult<T> items = service.search(criteria, first, pageSize);
			return Response.ok(items).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}
}
