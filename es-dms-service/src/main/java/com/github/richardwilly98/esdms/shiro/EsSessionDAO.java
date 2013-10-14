package com.github.richardwilly98.esdms.shiro;

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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;

import com.github.richardwilly98.esdms.SessionImpl;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.AuthenticationProvider;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.google.inject.Inject;

public class EsSessionDAO extends AbstractSessionDAO {

    Logger log = Logger.getLogger(this.getClass());

    final AuthenticationService authenticationService;

    @Inject
    public EsSessionDAO(final AuthenticationService authenticationService) {
	this.authenticationService = authenticationService;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
	try {
	    if (session == null || session.getId() == null) {
		log.warn("Session id is null.");
		return;
	    }
	    SessionImpl s = authenticationService.get(session.getId().toString());
	    if (s != null) {
		if (session.getAttribute(AuthenticationProvider.ES_DMS_ID_ATTRIBUTE) != null) {
		    s.setUserId(session.getAttribute(AuthenticationProvider.ES_DMS_ID_ATTRIBUTE).toString());
		}
		authenticationService.update(s);
	    } else {
		throw new UnknownSessionException(String.format("update session failed for %s", session.getId()));
	    }
	} catch (ServiceException ex) {
	    log.error("update failed", ex);
	}
    }

    @Override
    public void delete(Session session) {
	try {
	    SessionImpl s = authenticationService.get(session.getId().toString());
	    if (s != null) {
		authenticationService.delete(s);
	    }
	} catch (ServiceException ex) {
	    log.error("delete failed", ex);
	}
    }

    @Override
    public Collection<Session> getActiveSessions() {
	try {
	    log.trace("*** getActiveSessions");
	    Set<SessionImpl> sessions = authenticationService.getItems("active:true");
	    Set<Session> activeSessions = new HashSet<Session>();
	    for (com.github.richardwilly98.esdms.api.Session session : sessions) {
		activeSessions.add(new EsSession(session));
	    }
	    return activeSessions;
	} catch (ServiceException ex) {
	    log.error("delete failed", ex);
	}
	return null;
    }

    @Override
    protected Serializable doCreate(Session session) {
	try {
	    if (log.isTraceEnabled()) {
		log.trace(String.format("*** doCreate - %s", session));
	    }
	    Serializable sessionId = generateSessionId(session);
	    assignSessionId(session, sessionId);

	    SessionImpl s = new SessionImpl.Builder().id(sessionId.toString()).createTime(session.getStartTimestamp())
		    .lastAccessTime(session.getLastAccessTime()).active(true).timeout(session.getTimeout()).build();
	    s = authenticationService.create(s);
	    EsSession esSession = new EsSession(s);
	    return esSession.getId();
	} catch (ServiceException ex) {
	    log.error("doCreate failed", ex);
	}
	return null;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
	try {
	    // if (log.isTraceEnabled()) {
	    // log.trace(String.format("*** doReadSession - %s", sessionId));
	    // }
	    com.github.richardwilly98.esdms.api.Session session = authenticationService.get(sessionId.toString());
	    if (session == null) {
		return null;
	    }
	    return new EsSession(session);
	} catch (ServiceException ex) {
	    log.error("doReadSession failed", ex);
	}
	return null;
    }

}
