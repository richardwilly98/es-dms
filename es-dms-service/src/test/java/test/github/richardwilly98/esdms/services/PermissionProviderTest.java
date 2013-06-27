package test.github.richardwilly98.esdms.services;

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


import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.PermissionImpl;
import com.github.richardwilly98.esdms.api.Permission;

public class PermissionProviderTest extends ProviderTestBase {

	private Permission testCreatePermission(String name, String description,
			boolean disabled) throws Throwable {
		Permission permission = new PermissionImpl.Builder().id(name).description(description).disabled(disabled).build();
		
		Permission aPermission = permissionService.create(permission);
		Assert.assertEquals(permission.getId(), aPermission.getId());
		
		Permission newPermission = permissionService.get(aPermission.getId());
		Assert.assertNotNull(newPermission);
		Assert.assertEquals(permission.getName(), newPermission.getName());
		Assert.assertEquals(permission.getDescription(), newPermission.getDescription());
		Assert.assertEquals(permission.isDisabled(), newPermission.isDisabled());
		return newPermission;
	}

	@Test
	public void testCreatePermission() throws Throwable {
		log.info("Start testCreatePermission");

		// Make sure to be login with user having sufficient permission
		loginAdminUser();

		//content permissions
		testCreatePermission("profile:write", "write, create and edit document metadata", false);
		testCreatePermission("content:write", "write, create and edit document content", true);
		testCreatePermission("profile:read", "read", true);
		testCreatePermission("content:read", "read", true);
		testCreatePermission("access:read", "access control read", true);
		testCreatePermission("access:write", "access control write", true);
		testCreatePermission("profile:todelete", "mark profile as to delete", true);
		testCreatePermission("content:todelete", "mark content as to delete", true);
		testCreatePermission("profile:delete", "delete", true);
		testCreatePermission("content:delete", "delete", true);
		//user permissions
		testCreatePermission("user:create", "create", true);
		testCreatePermission("user:update", "user data update", true);
		testCreatePermission("user:add", "user access update", true);
		testCreatePermission("user:remove", "user access update", true);
		testCreatePermission("user:disable", "user access update", true);
		testCreatePermission("user:delete", "delete", true);
		//group permissions
		testCreatePermission("group:create", "create group", false);
		testCreatePermission("group:update", "group data update", true);
		testCreatePermission("group:add", "group access update", true);
		testCreatePermission("group:remove", "groupaccess update", true);
		testCreatePermission("group:disable", "group access update", true);
		testCreatePermission("group:delete", "delete group", false);
		
		testCreatePermission("group:add-user", "add user to group", false);
		testCreatePermission("group:remove-user", "remove user from group", false);
	}
	
	@Test
	public void testFindPermission() throws Throwable {
		log.info("Start testFindPermission");
		
		testCreatePermission("group:add-user", "add user to group", false);
		testCreatePermission("group:remove-user", "remove user from group", false);
		
		Permission permission = permissionService.get("group:add-user");
		
		Assert.assertNotNull(permission);
		if (!(permission == null) )log.info("permission found: " + permission.getName());
		
		permission = permissionService.get("group:remove-user");
		
		Assert.assertNotNull(permission);
		if (!(permission == null))log.info("permission found: " + permission.getName());
	}

	@Test
	public void testDeletePermission() throws Throwable {
		log.info("Start testDeletePermission");
		Permission p = testCreatePermission("write-annotation", "write", false);
		Permission permission = permissionService.get(p.getId());
		permissionService.delete(permission);
		permission = permissionService.get(p.getId());
		Assert.assertNull(permission);
	}
}
