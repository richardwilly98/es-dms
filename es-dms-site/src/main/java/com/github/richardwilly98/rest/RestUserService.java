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

import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.AuthenticationService;
import com.github.richardwilly98.api.services.HashService;
import com.github.richardwilly98.api.services.UserService;
import com.github.richardwilly98.rest.exception.RestServiceException;
import com.google.inject.Inject;

@Path("/users")
public class RestUserService extends RestServiceBase {

	private final HashService hashService;
	private final UserService userService;

	@Inject
	public RestUserService(AuthenticationService authenticationService, final HashService hashService, final UserService userService) {
		super(authenticationService);
		this.hashService = hashService;
		this.userService = userService;
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{id}")
	public Response get(@PathParam("id") String id) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("get - %s", id));
		}
		try {
			User user = userService.get(id);
			return Response.ok(user).build();
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
			User user = userService.get(id);
			userService.delete(user);
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
			List<User> users= userService.getList(name); 
			return  Response.ok(users).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(User user) {
		if (user == null) {
			throw new IllegalArgumentException("user");
		}
		if (log.isTraceEnabled()) {
			log.trace(String.format("create - %s", user));
		}
		try {
			if (user.getPassword() != null) {
				String encodedHash = hashService.toBase64(user.getPassword().getBytes());
				log.trace("From service - hash: " + encodedHash);
				user.setHash(encodedHash);
				user.setPassword(null);
			}
			user = userService.create(user);
			return Response.status(Status.CREATED).entity(user).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

}
