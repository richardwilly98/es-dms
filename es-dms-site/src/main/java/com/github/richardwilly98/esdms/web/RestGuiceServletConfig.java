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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;
import org.apache.shiro.guice.aop.ShiroAopModule;

import com.github.richardwilly98.esdms.inject.EsJerseyServletModule;
import com.github.richardwilly98.esdms.shiro.EsShiroWebModule;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.mycila.inject.jsr250.Jsr250;

public class RestGuiceServletConfig extends GuiceServletContextListener {

	private final Logger log = Logger.getLogger(this.getClass());

	private ServletContext servletContext;

	public static Injector injector;

	@Override
	protected Injector getInjector() {
		log.debug("*** getInjector ***");
		String securityFilterPath = "/api/*";
		injector = Jsr250.createInjector(new EsShiroWebModule(servletContext,
				securityFilterPath), new ShiroAopModule(),
				new EsJerseyServletModule(securityFilterPath));

		return injector;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		log.debug("*** contextInitialized ***");
		servletContext = servletContextEvent.getServletContext();
		super.contextInitialized(servletContextEvent);
	}

}
