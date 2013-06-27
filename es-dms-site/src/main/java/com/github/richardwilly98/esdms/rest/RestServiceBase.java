package com.github.richardwilly98.esdms.rest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.github.richardwilly98.esdms.rest.exception.UnauthorizedException;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.google.inject.Inject;

/*
 * CRUD methods MUST follow http response status code from http://www.restapitutorial.com/lessons/httpmethods.html
 */
public abstract class RestServiceBase {

	protected final Logger log = Logger.getLogger(getClass());
	protected final AuthenticationService authenticationService;

	@Context
	UriInfo url;

	@Inject
	public RestServiceBase(final AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
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

}
