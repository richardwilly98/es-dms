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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.elasticsearch.client.Client;

import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.exception.ServiceException;

@Singleton
public class RoleProvider extends ProviderBase<Role> implements RoleService {

    private final static String type = "role";

    @Inject
    RoleProvider(Client client, BootstrapService bootstrapService) throws ServiceException {
	super(client, bootstrapService, null, RoleProvider.type, Role.class);
    }

    @Override
    protected void loadInitialData() throws ServiceException {

	create(RoleService.DefaultRoles.READER.getRole());
	create(RoleService.DefaultRoles.WRITER.getRole());
	create(RoleService.DefaultRoles.ADMINISTRATOR.getRole());
        create(RoleService.DefaultRoles.PROCESS_ADMINISTRATOR.getRole());
        create(RoleService.DefaultRoles.PROCESS_USER.getRole());
    }

    @RequiresPermissions(RolePermissions.Constants.ROLE_CREATE)
    @Override
    public Role create(Role item) throws ServiceException {
	return super.create(item);
    }

    @RequiresPermissions(RolePermissions.Constants.ROLE_EDIT)
    @Override
    public Role update(Role item) throws ServiceException {
        checkNotNull(item, "item is null");
        checkArgument(item.getType() != RoleType.SYSTEM, String.format("Cannot update role %s with type %s", item.getId(), RoleType.SYSTEM));
        return super.update(item);
    }
    
    @RequiresPermissions(RolePermissions.Constants.ROLE_DELETE)
    @Override
    public void delete(Role item) throws ServiceException {
        checkNotNull(item, "item is null");
        checkArgument(item.getType() != RoleType.SYSTEM, String.format("Cannot delete role %s with type %s", item.getId(), RoleType.SYSTEM));
	super.delete(item);
    }

    @Override
    protected String getMapping() {
	return null;
    }

}
