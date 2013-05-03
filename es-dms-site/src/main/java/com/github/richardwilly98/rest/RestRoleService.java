package com.github.richardwilly98.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.richardwilly98.api.Role;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.AuthenticationService;
import com.github.richardwilly98.api.services.RoleService;
import com.github.richardwilly98.rest.exception.RestServiceException;
import com.google.inject.Inject;

@Path("/roles")
public class RestRoleService extends RestServiceBase {

	private final RoleService roleService;

	@Inject
	public RestRoleService(AuthenticationService authenticationService,
			final RoleService roleService) {
		super(authenticationService);
		this.roleService = roleService;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{id}")
	public Response get(@PathParam("id") String id) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("get - %s", id));
		}
		try {
			Role role = roleService.get(id);
			return Response.ok(role).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{id}")
	public Response delete(@PathParam("id") String id) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("get - %s", id));
		}
		try {
			Role role = roleService.get(id);
			roleService.delete(role);
			return Response.ok().build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/find/{name}")
	public Response find(@PathParam("name") String name) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("find - %s", name));
		}
		try {
			List<Role> roles = roleService.getList(name);
			return Response.ok(roles).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Role role) {
		if (role == null) {
			throw new IllegalArgumentException("role");
		}
		if (log.isTraceEnabled()) {
			log.trace(String.format("create - %s", role));
		}
		try {
			role = roleService.create(role);
			return Response.status(Status.CREATED).entity(role).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

}
