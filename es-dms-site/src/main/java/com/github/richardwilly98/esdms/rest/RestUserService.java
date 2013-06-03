package com.github.richardwilly98.esdms.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.HashService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.inject.Inject;

@Path(RestUserService.USERS_PATH)
public class RestUserService extends RestServiceBase<User> {

	public static final String USERS_PATH = "users";
	private final HashService hashService;

	@Inject
	public RestUserService(final AuthenticationService authenticationService,
			final HashService hashService, final UserService userService) {
		super(authenticationService, userService);
		this.hashService = hashService;
	}

	@Override
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(User item) {
		if (item.getPassword() != null) {
			String encodedHash = hashService.toBase64(item.getPassword()
					.getBytes());
			log.trace("From service - hash: " + encodedHash);
			item.setHash(encodedHash);
			item.setPassword(null);
		} else {
			log.warn("Missing password");
		}
		return super.create(item);
	}

	@Override
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") String id, User item) {
		if (item.getPassword() != null) {
			String encodedHash = hashService.toBase64(item.getPassword()
					.getBytes());
			log.trace("From service - hash: " + encodedHash);
			item.setHash(encodedHash);
			item.setPassword(null);
		}
		return super.update(id, item);
	}
}
