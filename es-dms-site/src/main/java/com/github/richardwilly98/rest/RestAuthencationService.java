package com.github.richardwilly98.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;

import com.sun.jersey.multipart.FormDataParam;

@Path("/auth")
public class RestAuthencationService {

	private static Logger log = Logger.getLogger(RestAuthencationService.class);

	@POST
	@Path("/login")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response login(@FormDataParam("username") String username,
			@FormDataParam("password") String password) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("login - %s - %s", username, password));
		}
		try {
			UsernamePasswordToken token = new UsernamePasswordToken(username,
					password);
			SecurityUtils.getSubject().login(token);
			return Response.status(Status.OK).build();
		} catch (Throwable t) {
			log.error("login failed", t);
			throw new RestServiceException(t.getLocalizedMessage());
		}
	}

}
