package com.github.richardwilly98.esdms.rest;

/*
 * #%L
 * es-dms-site
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.github.richardwilly98.esdms.rest.exception.UnauthorizedException;
import com.github.richardwilly98.esdms.services.AuthenticationService;

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

    protected boolean isAuthenticated() {
	return (getCurrentUser() != null);
    }

    protected String getCurrentUser() {
	try {
	    log.trace("*** getCurrentUser ***");
	    Subject currentSubject = SecurityUtils.getSubject();
	    log.trace(String.format("currentSubject.isAuthenticated(): %s", currentSubject.isAuthenticated()));
	    log.trace(String.format("Principal: %s", currentSubject.getPrincipal()));
	    if (currentSubject.getPrincipal() == null) {
		throw new UnauthorizedException("Unauthorize request", url.getPath());
	    } else {
		return currentSubject.getPrincipal().toString();
	    }
	} catch (Throwable t) {
	    throw new UnauthorizedException();
	}
    }

}
