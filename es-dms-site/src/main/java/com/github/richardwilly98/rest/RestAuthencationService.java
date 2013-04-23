package com.github.richardwilly98.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;

@Path("/auth")
public class RestAuthencationService {

	private static Logger log = Logger.getLogger(RestAuthencationService.class);

	@POST
	@Path("/login")
	public Response login(@FormParam("username") String username,
			@FormParam("password") String password) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("login - %s - %s", username, password));
		}
		try {
			UsernamePasswordToken token = new UsernamePasswordToken(username,
					password);
			SecurityUtils.getSubject().login(token);
			return Response.status(Status.OK).entity("SUCCESS").cookie(new NewCookie("LLL", "XXXX")).build();
		} catch (Throwable t) {
			log.error("login failed", t);
			throw new RestServiceException(t.getLocalizedMessage());
		}
	}

}
