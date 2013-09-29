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

import org.apache.log4j.Logger;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SimpleSessionFactory;
import org.apache.shiro.session.mgt.eis.SessionDAO;

import com.google.inject.Inject;

public class EsSessionManager extends DefaultSessionManager {

    private static Logger log = Logger.getLogger(EsSessionManager.class);

    @Inject
    public EsSessionManager(SessionDAO sessionDAO) {
	log.debug("*** constructor ***");
	this.setDeleteInvalidSessions(true);
	this.setSessionFactory(new SimpleSessionFactory());
	this.setSessionDAO(sessionDAO);
    }
}
