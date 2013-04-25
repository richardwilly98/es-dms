package com.github.richardwilly98.rest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;

abstract class RestServiceBase {

	Logger log = Logger.getLogger(this.getClass());

	protected void isAuthenticated() {
		log.debug("Principal: " + SecurityUtils.getSubject().getPrincipal());
		if (SecurityUtils.getSubject().getPrincipal() == null)
			throw new UnauthorizedException("Unauthorize request", "/documents");
	}
}
