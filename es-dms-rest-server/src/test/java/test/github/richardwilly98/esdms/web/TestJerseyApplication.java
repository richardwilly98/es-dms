package test.github.richardwilly98.esdms.web;

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

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

public class TestJerseyApplication extends ResourceConfig {

    @Inject
    public TestJerseyApplication(ServiceLocator serviceLocator) {
	super(MultiPartFeature.class);
	packages("com.github.richardwilly98.esdms.rest", "com.github.richardwilly98.esdms.web", "com.fasterxml.jackson.jaxrs");

	System.out.println("Registering injectables...");

	GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);

	GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
	// guiceBridge
	// .bridgeGuiceInjector(Guice.createInjector(new
	// EsJerseyServletModule("/api/*")));
	guiceBridge.bridgeGuiceInjector(TestRestGuiceServletConfig.injector);
    }

}
