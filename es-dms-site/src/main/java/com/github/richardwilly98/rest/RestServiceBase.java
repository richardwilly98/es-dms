package com.github.richardwilly98.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.github.richardwilly98.api.ItemBase;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.AuthenticationService;
import com.github.richardwilly98.api.services.BaseService;
import com.github.richardwilly98.rest.exception.RestServiceException;
import com.google.inject.Inject;

/*
 * CRUD methods MUST follow http response status code from http://www.restapitutorial.com/lessons/httpmethods.html
 */
public abstract class RestServiceBase<T extends ItemBase> {

	public static final String FIND_PATH = "find";
	
	protected final Logger log = Logger.getLogger(getClass());
	protected final AuthenticationService authenticationService;
	protected final BaseService<T> service;

	@Context
	UriInfo url;

	@Inject
	public RestServiceBase(final AuthenticationService authenticationService, final BaseService<T> service) {
		this.authenticationService = authenticationService;
		this.service = service;
	}

	private String currentUser;

	protected void isAuthenticated() {
		try {
			log.debug("*** isAuthenticated ***");
			Subject currentSubject = SecurityUtils.getSubject();
			log.debug("currentSubject.isAuthenticated(): "
					+ currentSubject.isAuthenticated());
			log.debug("Principal: " + currentSubject.getPrincipal());
			if (currentSubject.getPrincipal() == null) {
				throw new UnauthorizedException("Unauthorize request",
						url.getPath());
			} else {
				if (currentUser == null) {
					currentUser = currentSubject.getPrincipal().toString();
				}
			}
		} catch (Throwable t) {
			throw new UnauthorizedException();
		}
	}

	protected String getCurrentUser() {
		if (currentUser == null) {
			isAuthenticated();
		}
		return currentUser;
	}
	
	protected URI getItemUri(T item) {
		checkNotNull(item);
		return url.getBaseUriBuilder().path(getClass()).path(item.getId()).build();
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
			return Response
					.created(
							getItemUri(item)).build();
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
			service.delete(item);
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
	@Path(FIND_PATH + "/{name}")
	public Response find(@PathParam("name") String name) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("find - %s", name));
		}
		try {
			List<T> items = service.getList(name);
			return Response.ok(items).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}
}
