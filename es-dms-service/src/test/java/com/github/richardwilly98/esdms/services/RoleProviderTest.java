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

import static com.google.common.collect.Sets.newHashSet;

import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.search.api.SearchResult;
import com.github.richardwilly98.esdms.api.User;

public class RoleProviderTest extends ProviderTestBase {

    private String testCreateRole(String name, String description, Set<Permission> permissions, boolean disabled) throws Throwable {

        Role role = createRole(name, description, disabled, permissions);

        log.trace("role created: " + role.getId());
        Assert.assertNotNull(role);
        log.trace("role name: " + role.getName());
        Assert.assertEquals(name, role.getName());
        log.trace("role description: " + role.getDescription());
        Assert.assertEquals(description, role.getDescription());
        log.trace("role disabled: " + role.isDisabled());
        Assert.assertEquals(disabled, role.isDisabled());

        return role.getId();
    }

    @Test
    public void testCreateRole() throws Throwable {
        log.info("Start testCreateRole");

        // Make sure to be login with user having sufficient permission
        loginAdminUser();

        Set<Permission> permissions = newHashSet();

        permissions.add(createPermission("profile:read", "profile:read", true));
        permissions.add(createPermission("content:read", "content:read", true));
        permissions.add(createPermission("annotation:read", "annotation:read", true));
        permissions.add(createPermission("annotation:write", "annotation:write", true));
        permissions.add(createPermission("comment:read", "comment:read", true));
        permissions.add(createPermission("comment:write", "comment:write", true));
        permissions.add(createPermission("content:todelete", "content:todelete", true));
        testCreateRole("Proof-Reader", "reader", permissions, false);
        log.info("Proof-Reader permissions count: " + permissions.size());
        Assert.assertEquals(permissions.size(), 7);
        permissions.add(createPermission("profile:write", "profile:write", true));
        permissions.add(createPermission("content:write", "content:write", true));
        permissions.add(createPermission("content:add", "content:add", true));
        permissions.add(createPermission("content:remove", "content:remove", true));
        permissions.add(createPermission("profile:todelete", "profile:todelete", true));
        testCreateRole("Writer", "writer", permissions, false);
        log.info("Writer permissions count: " + permissions.size());
        Assert.assertEquals(permissions.size(), 12);
        permissions.add(createPermission("user:add", "user:add", true));
        permissions.add(createPermission("user:remove", "user:remove", true));
        permissions.add(createPermission("group:add", "group:add", true));
        permissions.add(createPermission("group:remove", "group:remove", true));
        permissions.add(createPermission("role:add", "role:add", true));
        permissions.add(createPermission("role:remove", "role:remove", true));
        testCreateRole("Editor", "Editor", permissions, false);
        log.info("Editor permissions count: " + permissions.size());
        Assert.assertEquals(permissions.size(), 18);
        permissions.add(createPermission("milestone:add", "milestone:add", true));
        permissions.add(createPermission("milestone:remove", "milestone:remove", true));
        permissions.add(createPermission("task:assign", "task:assign", true));
        testCreateRole("Coordinator", "coordinator", permissions, false);
        log.info("Coordinator permissions count: " + permissions.size());
        Assert.assertEquals(permissions.size(), 21);

        log.info("Start testCreateRole completed!!");
    }

    @Test
    public void testSystemRole() throws Throwable {
        log.info("Start testSystemRole");
        String name = "system-role-" + System.currentTimeMillis();
        Role role = createRole(name, "System role", false, RoleType.SYSTEM, null);
        Assert.assertNotNull(role);
        try {
            role.setName("System role - update");
            roleService.update(role);
            Assert.fail("Should not be able to update " + RoleType.SYSTEM + " role type");
        } catch (IllegalArgumentException ex) {

        }
        try {
            roleService.delete(role);
            Assert.fail("Should not be able to delete " + RoleType.SYSTEM + " role type");
        } catch (IllegalArgumentException ex) {

        }
    }

    @Test
    public void testFindRolesByType() throws Throwable {
        log.info("Start testFindRolesByType");
        SearchResult<Role> roles = roleService.findByType(RoleType.SYSTEM, 0, 20);
        Assert.assertNotNull(roles);
        Assert.assertNotNull(roles.getTotalHits() > 0);
        for(Role role : roles.getItems()) {
            Assert.assertEquals(role.getType(), RoleType.SYSTEM);
            log.trace(role);
        }
        
        String name = "role-" + System.currentTimeMillis();
        Role userDefinedRole = createRole(name, "", false, RoleType.USER_DEFINED, null);
        roles = roleService.findByType(RoleType.USER_DEFINED, 0, 20);
        Assert.assertNotNull(roles);
        Assert.assertNotNull(roles.getTotalHits() > 0);
        Assert.assertTrue(roles.getItems().contains(userDefinedRole));

        Role processRole = createRole(name, "", false, RoleType.PROCESS, null);
        roles = roleService.findByType(RoleType.PROCESS, 0, 20);
        Assert.assertNotNull(roles);
        Assert.assertNotNull(roles.getTotalHits() > 0);
        Assert.assertTrue(roles.getItems().contains(processRole));
        Assert.assertFalse(roles.getItems().contains(userDefinedRole));
    }

    @Test
    public void testFindRole() throws Throwable {
        log.info("Start testFindRole");

        Set<Permission> permissions = new HashSet<Permission>();

        permissions.add(createPermission("profile:read", "profile:read", true));
        permissions.add(createPermission("content:read", "content:read", true));
        permissions.add(createPermission("annotation:read", "annotation:read", true));
        permissions.add(createPermission("annotation:write", "annotation:write", true));
        permissions.add(createPermission("comment:read", "comment:read", true));
        permissions.add(createPermission("comment:write", "comment:write", true));
        permissions.add(createPermission("content:todelete", "content:todelete", true));
        testCreateRole("Proof-Reader", "reader", permissions, false);
        log.info("Proof-Reader permissions count: " + permissions.size());

        Role role = roleService.get("Proof-Reader");

        Assert.assertNotNull(role);
        if (!(role == null))
            log.info("Role found: " + role.getName());

        permissions.add(createPermission("profile:write", "profile:write", true));
        permissions.add(createPermission("content:write", "content:write", true));
        permissions.add(createPermission("content:add", "content:add", true));
        permissions.add(createPermission("content:remove", "content:remove", true));
        permissions.add(createPermission("profile:todelete", "profile:todelete", true));
        testCreateRole("writer", "writer", permissions, false);
        log.info("Writer permissions count: " + permissions.size());

        role = roleService.get("Writer");

        Assert.assertNotNull(role);
        if (!(role == null))
            log.info("Role found: " + role.getName());
    }

    @Test
    public void testAddRoletoUser() throws Throwable {
        log.info("Start testAddRoletoUser");
        User user = userService.get("richard.louapre@gmail.com");
        if (user == null)
            log.error("Failed to retrieve user richard.louapre@gmail.com!!");
        Assert.assertNotNull(user);

        Role role = roleService.get("collaborator");
        if (role == null)
            log.error("Failed to retrieve role collaborator!!");
        Assert.assertNotNull(role);

        log.info("Roles for user: " + user.getName());
        for (Role r : user.getRoles())
            log.info(r.getId());

        user.removeRole(role);
        for (Role r : user.getRoles())
            log.info(r.getId());

        user.addRole(role);
        for (Role r : user.getRoles())
            log.info(r.getId());
        log.info("End of testAddRoletoUser");
    }

    @Test
    public void testDeleteRole() throws Throwable {

        log.info("Start testDeleteRole");

        Set<Permission> permissions = newHashSet();

        log.info("Creating roles - ");
        permissions.add(createPermission("profile:read", "profile:read", true));
        permissions.add(createPermission("content:read", "content:read", true));
        permissions.add(createPermission("annotation:read", "annotation:read", true));
        permissions.add(createPermission("annotation:write", "annotation:write", true));
        permissions.add(createPermission("comment:read", "comment:read", true));
        permissions.add(createPermission("comment:write", "comment:write", true));
        permissions.add(createPermission("content:todelete", "content:todelete", true));
        testCreateRole("Proof-Reader", "reader", permissions, false);

        permissions.add(createPermission("profile:write", "profile:write", true));
        permissions.add(createPermission("content:write", "content:write", true));
        permissions.add(createPermission("content:add", "content:add", true));
        permissions.add(createPermission("content:remove", "content:remove", true));
        permissions.add(createPermission("profile:todelete", "profile:todelete", true));
        testCreateRole("dummy-writer", "Dummy-Writer", permissions, false);

        permissions.add(createPermission("user:add", "user:add", true));
        permissions.add(createPermission("user:remove", "user:remove", true));
        permissions.add(createPermission("group:add", "group:add", true));
        permissions.add(createPermission("group:remove", "group:remove", true));
        permissions.add(createPermission("role:add", "role:add", true));
        permissions.add(createPermission("role:remove", "role:remove", true));
        testCreateRole("Editor", "Editor", permissions, false);

        permissions.add(createPermission("milestone:add", "milestone:add", true));
        permissions.add(createPermission("milestone:remove", "milestone:remove", true));
        permissions.add(createPermission("task:assign", "task:assign", true));
        testCreateRole("Coordinator", "coordinator", permissions, false);

        log.info("Roles created..");

        Role role = roleService.get("collaborator");
        if (role == null)
            log.error("Failed to retrieve role collaborator!!");
        Assert.assertNotNull(role);

        SearchResult<Role> searchResult = roleService.search("*", 0, -1);

        Set<Role> roles = searchResult.getItems();

        log.info("List of available roles: ");
        for (Role r : roles)
            log.info(r.getId());

        log.info("Deleting role: " + role.getId());
        roleService.delete(role);

        log.info("List of remaining roles: ");
        for (Role r : roles)
            log.info(r.getId());

        log.info("Obtaining role: dummy-writer");
        role = roleService.get("dummy-writer");
        Assert.assertNotNull(role);

        log.info("Deleting role: " + role.getId());
        roleService.delete(role);

        log.info("List of remaining roles: ");
        for (Role r : roles)
            log.info(r.getId());

        log.info("End of testDeleteRole");
    }

}
