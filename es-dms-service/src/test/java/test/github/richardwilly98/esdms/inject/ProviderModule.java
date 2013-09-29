package test.github.richardwilly98.esdms.inject;

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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.guice.aop.ShiroAopModule;

import test.github.richardwilly98.esdms.inject.TestEsClientModule;

import com.github.richardwilly98.esdms.inject.BootstrapModule;
import com.github.richardwilly98.esdms.inject.ServicesModule;
import com.github.richardwilly98.esdms.shiro.EsShiroModule;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.mycila.inject.jsr250.Jsr250;

public class ProviderModule extends AbstractModule {

    @Override
    protected void configure() {

	install(new BootstrapModule());
	install(new TestEsClientModule());
	install(new ServicesModule());
	install(new ShiroAopModule());
	install(new EsShiroModule());

	Injector injector = Jsr250.createInjector(new BootstrapModule(), new TestEsClientModule(), new ServicesModule(),
	        new EsShiroModule());
	org.apache.shiro.mgt.SecurityManager securityManager = injector.getInstance(org.apache.shiro.mgt.SecurityManager.class);
	SecurityUtils.setSecurityManager(securityManager);
    }

}
