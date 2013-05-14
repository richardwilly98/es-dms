package com.github.richardwilly98.rest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.github.richardwilly98.api.services.AuthenticationService;
import com.google.inject.Inject;

abstract class RestServiceBase {

	Logger log = Logger.getLogger(this.getClass());
	
	final AuthenticationService authenticationService;

	@Inject
	public RestServiceBase(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService; 
	}
	
	private String currentUser;

	protected void isAuthenticated() {
		log.debug("*** isAuthenticated ***");
		Subject currentSubject = SecurityUtils.getSubject();
		log.debug("currentSubject.isAuthenticated(): " + currentSubject.isAuthenticated());
		log.debug("Principal: " + currentSubject.getPrincipal());
		if (currentSubject.getPrincipal() == null) {
			throw new UnauthorizedException("Unauthorize request", "/documents");
		} else {
			if (currentUser == null) {
				currentUser = currentSubject.getPrincipal().toString();
			}
		}
	}
	
	protected String getCurrentUser() {
		if (currentUser == null) {
			isAuthenticated();
		}
		return currentUser;
	}
}
