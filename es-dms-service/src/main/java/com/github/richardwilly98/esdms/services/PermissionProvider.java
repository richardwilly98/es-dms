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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.client.Client;

import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.exception.ServiceException;

@Singleton
public class PermissionProvider extends ProviderBase<Permission> implements PermissionService {

    private final static String type = "permission";

    @Inject
    PermissionProvider(Client client, BootstrapService bootstrapService) throws ServiceException {
	super(client, bootstrapService, null, PermissionProvider.type, Permission.class);
    }

    @Override
    protected void loadInitialData() throws ServiceException {
        create(DocumentService.DocumentPermissions.CREATE_PERMISSION.getPermission());
        create(DocumentService.DocumentPermissions.DELETE_PERMISSION.getPermission());
        create(DocumentService.DocumentPermissions.EDIT_PERMISSION.getPermission());
        create(DocumentService.DocumentPermissions.READ_PERMISSION.getPermission());
        create(UserService.UserPermissions.CREATE_PERMISSION.getPermission());
        create(UserService.UserPermissions.EDIT_PERMISSION.getPermission());
        create(UserService.UserPermissions.DELETE_PERMISSION.getPermission());
        create(RoleService.RolePermissions.CREATE_PERMISSION.getPermission());
        create(RoleService.RolePermissions.EDIT_PERMISSION.getPermission());
        create(RoleService.RolePermissions.DELETE_PERMISSION.getPermission());
    }

    @Override
    protected String getMapping() {
	return null;
    }

}
