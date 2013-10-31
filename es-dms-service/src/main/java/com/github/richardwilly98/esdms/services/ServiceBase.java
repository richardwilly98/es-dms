package com.github.richardwilly98.esdms.services;

/*
 * #%L
 * es-dms-service
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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Settings;
import com.github.richardwilly98.esdms.exception.ServiceException;

public abstract class ServiceBase {

    final protected Logger log = Logger.getLogger(getClass());

    final static ObjectMapper mapper = new ObjectMapper();

    final Client client;
    final Settings settings;
    final String index;
    final String type;

    @Inject
    ServiceBase(final Client client, final BootstrapService bootstrapService, final String index, final String type)
            throws ServiceException {
        checkNotNull(client);
        checkNotNull(bootstrapService);
        checkNotNull(type);
        this.client = client;
        this.settings = bootstrapService.loadSettings();
        if (index != null) {
            this.index = index;
        } else {
            this.index = settings.getLibrary();
        }
        this.type = type;
    }

    protected String isAuthenticated() throws ServiceException {
        try {
            String currentUser = null;
            Subject currentSubject = SecurityUtils.getSubject();
            if (currentSubject.getPrincipal() == null) {
                throw new ServiceException("Unauthorize request");
            } else {
                if (currentSubject.getPrincipal() instanceof UserImpl) {
                    currentUser = ((UserImpl) currentSubject.getPrincipal()).getId();
                }
            }
            return currentUser;
        } catch (Throwable t) {
            throw new ServiceException("Could not authenticate request");
        }
    }

    protected String getCurrentUser() throws ServiceException {
        return isAuthenticated();
    }

}
