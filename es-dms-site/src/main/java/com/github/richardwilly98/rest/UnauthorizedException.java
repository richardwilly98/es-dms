package com.github.richardwilly98.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@SuppressWarnings("serial")
public class UnauthorizedException extends WebApplicationException {

	public UnauthorizedException() {
		this("Please authenticate.", "Name of your web service");
	}

	public UnauthorizedException(String message, String realm) {
		super(Response
				.status(Status.UNAUTHORIZED)
//				.header(HttpHeaders.WWW_AUTHENTICATE,
//						"Basic realm=\"" + realm + "\"")
						.entity(message)
				.build());
	}
}
