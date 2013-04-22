package com.github.richardwilly98.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.github.richardwilly98.inject.ProviderModule;

import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.services.UserProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Path("/users")
public class RestUserService {

	private static Logger log = Logger.getLogger(RestUserService.class);

	private UserProvider provider;

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
	public User get(@PathParam("id") String id) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("get - %s", id));
		}
		try {
			return getProvider().getUser(id);
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/find/{name}")
	public List<User> find(@PathParam("name") String name) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("find - %s", name));
		}
		try {
			return getProvider().getUsers(name);
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
			String id = getProvider().createUser(user);
			user.setId(id);
			return Response.status(201).entity(user).build();
		} catch (ServiceException e) {
			throw new RestServiceException(e.getLocalizedMessage());
		}
	}

}
