package com.github.richardwilly98.esdms.web;

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

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.RestItemBaseService;
import com.github.richardwilly98.esdms.services.AuditService;

@Audit
@Provider
public class AuditFilter implements ContainerResponseFilter {

    private static Logger log = Logger.getLogger(AuditFilter.class);
    private final AuditService service;

    @Inject
    public AuditFilter(final AuditService service) {
        this.service = service;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Annotation[] annotations = responseContext.getEntityAnnotations();
        if (annotations == null) {
            log.info("No annotation found.");
        } else {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Audit.class) {
                    String id = responseContext.getHeaderString(RestItemBaseService.ITEM_ID_HEADER);
                    if (id != null) {
                        String currentUser = getCurrentUser();
                        if (currentUser != null) {
                            try {
                                if (log.isDebugEnabled()) {
                                    log.debug("Audit value: " + ((Audit) annotation).value() + " - id: " + id + " - current user: "
                                            + currentUser);
                                }
                                service.create(((Audit) annotation).value(), id, currentUser);
                            } catch (ServiceException sEx) {
                                log.error("Create audit failed.", sEx);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private String getCurrentUser() {
        try {
            Subject currentSubject = SecurityUtils.getSubject();
            if (currentSubject.getPrincipal() == null) {
                return null;
            } else {
                return ((User) currentSubject.getPrincipal()).getId();
            }
        } catch (Throwable t) {
            return null;
        }
    }
}
