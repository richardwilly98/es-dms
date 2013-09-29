package com.github.richardwilly98.esdms.rest.exception;

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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@SuppressWarnings("serial")
public class UnauthorizedException extends WebApplicationException {

    public UnauthorizedException() {
	this("Please authenticate.", "Name of your web service");
    }

    public UnauthorizedException(String message, String realm) {
	super(Response.status(Status.UNAUTHORIZED)
	// .header(HttpHeaders.WWW_AUTHENTICATE,
	// "Basic realm=\"" + realm + "\"")
	        .entity(message).build());
    }
}
