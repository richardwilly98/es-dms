package com.github.richardwilly98.rest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;

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
		log.debug("Principal: " + SecurityUtils.getSubject().getPrincipal());
		if (SecurityUtils.getSubject().getPrincipal() == null) {
			throw new UnauthorizedException("Unauthorize request", "/documents");
		} else {
			if (currentUser == null) {
				currentUser = SecurityUtils.getSubject().getPrincipal().toString();
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
