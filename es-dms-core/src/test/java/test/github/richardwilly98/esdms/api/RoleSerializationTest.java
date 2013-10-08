package test.github.richardwilly98.esdms.api;

/*
 * #%L
 * es-dms-core
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

import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.PermissionImpl;
import com.github.richardwilly98.esdms.RoleImpl;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.services.RoleService.DefaultRoles;
import com.google.common.collect.ImmutableSet;

public class RoleSerializationTest {

    private static Logger log = Logger.getLogger(RoleSerializationTest.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    protected Permission createPermission(String name, String description, boolean disabled) {
        Assert.assertTrue(!(name == null || name.isEmpty()));
        return new PermissionImpl.Builder().id(name).name(name).description(description).disabled(disabled).build();
    }

    protected Role createRole(String name, String description, boolean disabled, RoleType type, Set<Permission> permissions) {
        log.trace("Preparing to create permission: " + name);
        Assert.assertTrue(!(name == null || name.isEmpty()));
        return new RoleImpl.Builder().id(name).name(name).description(description).disabled(disabled).type(type).permissions(permissions).build();
    }

    @Test
    public void testSerializeDeserializeRoleNoPermission() throws Throwable {
        log.debug("*** testSerializeDeserializeRoleNoPermission ***");
        String id = "role-" + System.currentTimeMillis();
        String name = id;
        Role role = createRole(name, "Dummy Role", false, RoleType.USER_DEFINED, null);
        log.debug(role);
        String json = mapper.writeValueAsString(role);
        log.debug(json);
        Assert.assertNotNull(json);
        Role role2 = mapper.readValue(json, Role.class);
        log.debug(role2);
        Assert.assertEquals(role, role2);
        
        Role adminRole = DefaultRoles.ADMINISTRATOR.getRole();
        json = mapper.writeValueAsString(adminRole);
        Assert.assertNotNull(json);
        Role role3 = mapper.readValue(json, Role.class);
        Assert.assertEquals(adminRole, role3);
    }

    @Test
    public void testSerializeDeserializeRoleWithPermission() throws Throwable {
        log.debug("*** testSerializeDeserializeRoleWithPermission ***");
        String id = "role-" + System.currentTimeMillis();
        String name = id;
        Permission perm1 = createPermission("permission-1", "Permission #1", false);
        Permission perm2 = createPermission("permission-2", "Permission #2", false);
        Role role = createRole(name, "Dummy Role", false, RoleType.SYSTEM, ImmutableSet.of(perm1));
        log.debug(role);
        Assert.assertTrue(role.getPermissions().contains(perm1));
        Assert.assertFalse(role.getPermissions().contains(perm2));
        role.addPermission(perm2);
        Assert.assertTrue(role.getPermissions().contains(perm2));
        role.removePermission(perm2);
        Assert.assertFalse(role.getPermissions().contains(perm2));
        String json = mapper.writeValueAsString(role);
        log.debug(json);
        Assert.assertNotNull(json);
        Role role2 = mapper.readValue(json, Role.class);
        log.debug(role2);
        Assert.assertEquals(role, role2);
    }

    @Test
    public void testUserHasRole() throws Throwable {
	log.debug("*** testUserHasRole ***");
	String id = "user-" + System.currentTimeMillis();
	String name = id;
	String email = id + "@gmail.com";
	char[] password = "secret".toCharArray();
	Role role = createRole("my-role", "My Role", false, RoleType.USER_DEFINED, null);
	User user = new UserImpl.Builder().id(id).name(name).email(email).password(password).login(email).roles(ImmutableSet.of(role)).build();
	log.debug("user: " + user);
	Assert.assertTrue(user.hasRole(role));
	String json = mapper.writeValueAsString(user);
	log.debug(json);
	Assert.assertNotNull(json);
	User user2 = mapper.readValue(json, User.class);
	log.debug("user2: " + user2);
	Assert.assertEquals(user, user2);
	Assert.assertTrue(user2.hasRole(role));
    }

}
