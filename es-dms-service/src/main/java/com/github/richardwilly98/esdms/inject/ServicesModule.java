package com.github.richardwilly98.esdms.inject;

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

import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.services.AuditProvider;
import com.github.richardwilly98.esdms.services.AuditService;
import com.github.richardwilly98.esdms.services.AuthenticationProvider;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.DocumentProvider;
import com.github.richardwilly98.esdms.services.DocumentService;
import com.github.richardwilly98.esdms.services.HashService;
import com.github.richardwilly98.esdms.services.ParameterProvider;
import com.github.richardwilly98.esdms.services.ParameterService;
import com.github.richardwilly98.esdms.services.PermissionProvider;
import com.github.richardwilly98.esdms.services.PermissionService;
import com.github.richardwilly98.esdms.services.RatingProvider;
import com.github.richardwilly98.esdms.services.RatingService;
import com.github.richardwilly98.esdms.services.RoleProvider;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.SHA512HashProvider;
import com.github.richardwilly98.esdms.services.SearchProvider;
import com.github.richardwilly98.esdms.services.SearchService;
import com.github.richardwilly98.esdms.services.UserProvider;
import com.github.richardwilly98.esdms.services.UserService;
import com.github.richardwilly98.esdms.services.VersionProvider;
import com.github.richardwilly98.esdms.services.VersionService;
import com.github.richardwilly98.esdms.services.audit.AuditStrategy;
import com.github.richardwilly98.esdms.services.audit.SimpleAuditStrategy;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

public class ServicesModule extends AbstractModule {

    @Override
    protected void configure() {

	bind(HashService.class).to(SHA512HashProvider.class).asEagerSingleton();
        bind(ParameterService.class).to(ParameterProvider.class).asEagerSingleton();
	bind(AuthenticationService.class).to(AuthenticationProvider.class).asEagerSingleton();
	bind(PermissionService.class).to(PermissionProvider.class).asEagerSingleton();
	bind(RoleService.class).to(RoleProvider.class).asEagerSingleton();
	bind(UserService.class).to(UserProvider.class).asEagerSingleton();
	bind(DocumentService.class).to(DocumentProvider.class).asEagerSingleton();
	bind(VersionService.class).to(VersionProvider.class).asEagerSingleton();
	bind(AuditService.class).to(AuditProvider.class).asEagerSingleton();
	bind(AuditStrategy.class).to(SimpleAuditStrategy.class).asEagerSingleton();
	bind(new TypeLiteral<SearchService<Document>>() {
	}).to(SearchProvider.class).asEagerSingleton();
	bind(RatingService.class).to(RatingProvider.class).asEagerSingleton();
    }

}
