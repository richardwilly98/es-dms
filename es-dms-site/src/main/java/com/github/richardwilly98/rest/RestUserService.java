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
import com.github.richardwilly98.inject.ProviderModule;
import com.github.richardwilly98.rest.exception.RestServiceException;
import com.github.richardwilly98.service.HashService;
import com.github.richardwilly98.services.UserProvider;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

@Path("/users")
public class RestUserService extends RestServiceBase {

	private final HashService service;
	private UserProvider provider;

	@Inject
	public RestUserService(final HashService service) {
		this.service = service;
	}
	
	private UserProvider getProvider() {
		if (provider == null) {
			Injector injector = Guice.createInjector(new ProviderModule());
			provider = injector.getInstance(UserProvider.class);
		}
		return provider;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{id}")
	public Response get(@PathParam("id") String id) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("get - %s", id));
		}
		try {
			User user = getProvider().get(id);
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
			User user = getProvider().get(id);
			getProvider().delete(user);
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
			List<User> users= getProvider().getList(name); 
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
				String encodedHash = service.toBase64(user.getPassword().getBytes());
				log.trace("From service - hash: " + encodedHash);
				user.setHash(encodedHash);
				user.setPassword(null);
			}
			String id = getProvider().create(user);
			user.setId(id);
			return Response.status(Status.CREATED).entity(user).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

}
