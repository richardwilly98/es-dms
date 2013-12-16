package com.github.richardwilly98.esdms.web;

/*
 * #%L
 * es-dms-rest-server
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


import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
//import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
//import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

//@PreMatching
//@Provider
public class PreMatchingFilter implements ContainerRequestFilter {

    private static Logger log = Logger.getLogger(PreMatchingFilter.class);
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        log.debug("*** filter ***");
        Request request = requestContext.getRequest();
        if (request != null) {
            log.debug(request);
        } else {
            log.info("Request is null");
        }
        log.info("Listing properties");
        for(String name : requestContext.getPropertyNames()) {
            log.debug("Property " + name + " - " + requestContext.getProperty(name) );
        }
        
        UriInfo uriInfo = requestContext.getUriInfo();
        log.info("UriInfo: " + uriInfo.getRequestUri());
        List<PathSegment> segments = uriInfo.getPathSegments();
        log.info("Listing segments");
        for (PathSegment segment : segments) {
            log.debug("Segment " + segment);
            for(String key: segment.getMatrixParameters().keySet()) {
                log.debug("Matrix parameter " + key +" - " + segment.getMatrixParameters().getFirst(key));
            }
        }
        if (uriInfo.getQueryParameters() != null) {
            log.info("Listing query parameters");
            for(String key: uriInfo.getQueryParameters().keySet()) {
                log.debug("Query parameter " + key + " - " + uriInfo.getQueryParameters().getFirst(key));
            }
        }
        if (uriInfo.getMatchedURIs() != null) {
            log.info("Listing matched uris");
            for(String uri: uriInfo.getMatchedURIs()) {
                log.debug("Matched uri " + uri);
            }
        }
    }

}
