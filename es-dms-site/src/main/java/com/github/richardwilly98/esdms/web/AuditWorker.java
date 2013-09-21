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


import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

import com.github.richardwilly98.esdms.api.AuditEntry;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.AuditService;

@Singleton
public class AuditWorker implements Runnable {

	private static Logger log = Logger.getLogger(AuditWorker.class);
	private final AuditService service;
	
	@Inject
	public AuditWorker(final AuditService service) {
		this.service = service;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(5000);
				log.debug("Run at " + new Date());
				try {
					String criteria = "event:" + AuditEntry.Event.UPLOAD;
					SearchResult<AuditEntry> result = service.search(criteria, 0, 20);
					log.debug(String.format("TotalHits for %s: %s", criteria, result.getTotalHits()));
				} catch (ServiceException sEx) {
					log.error("AuditService.search failed.", sEx);
				}
			} catch (InterruptedException iEx) {
				
			}
		}
	}

}
