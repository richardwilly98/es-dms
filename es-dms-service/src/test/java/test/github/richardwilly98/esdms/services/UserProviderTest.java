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

import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.common.collect.ImmutableSet;

public class UserProviderTest extends ProviderTestBase {

    private String testCreateUser(String name, String description, boolean disabled, String email, char[] password, Set<Role> roles)
            throws Throwable {
        User user = createUser(name, description, disabled, email, password, roles);
        Assert.assertNotNull(user);
        Assert.assertEquals(name, user.getName());
        Assert.assertEquals(description, user.getDescription());
        Assert.assertEquals(disabled, user.isDisabled());
        Assert.assertEquals(email, user.getEmail());
        if (roles != null) {
            Assert.assertTrue(user.getRoles().equals(roles));
        } else {
            Assert.assertTrue(user.getRoles().contains(RoleService.DefaultRoles.DEFAULT.getRole()));
        }
        return user.getId();
    }

    private void deleteUser(String id) throws ServiceException {
        User user = userService.get(id);
        if (user != null) {
            userService.delete(user);
        }
    }

    @Test
    public void testCreateUser() throws Throwable {
        log.info("Start testCreateUser");

        // Make sure to be login with user having sufficient permission
        loginAdminUser();
        String id = testCreateUser("Richard", "Lead developer", false, "richard@pippo.pippo", "qwerty".toCharArray(), null);
        deleteUser(id);
        id = testCreateUser("Danilo", "Mezza calzetta", true, "danilo@pippo.pippo", "123456".toCharArray(), null);
        deleteUser(id);
    }

    @Test
    public void testCannotDeleteAdmin() throws Throwable {
        log.info("Start testCannotDeleteAdmin");

        // Make sure to be login with user having sufficient permission
        loginAdminUser();
        try {
            User adminUser = userService.get(UserService.DEFAULT_ADMIN_LOGIN);
            Assert.assertNotNull(adminUser);
            userService.delete(adminUser);
            Assert.fail("Must not be able to delete admin user");
        } catch (IllegalArgumentException ex) {

        }
    }

    @Test
    public void testCannotRemoveSystemRolesFromAdmin() throws Throwable {
        log.info("Start testCannotRemoveSystemRolesFromAdmin");

        // Make sure to be login with user having sufficient permission
        loginAdminUser();
        User adminUser = userService.get(UserService.DEFAULT_ADMIN_LOGIN);
        Assert.assertNotNull(adminUser);
        try {
            Assert.assertTrue(adminUser.getRoles().contains(RoleService.DefaultRoles.ADMINISTRATOR.getRole()));
            adminUser.removeRole(RoleService.DefaultRoles.ADMINISTRATOR.getRole());
            userService.update(adminUser);
            Assert.fail("Must not be able to remove " + RoleService.DefaultRoles.Constants.ADMINISTRATOR_ROLE_ID + " from admin user");
        } catch (IllegalArgumentException ex) {

        }
        try {
            Assert.assertTrue(adminUser.getRoles().contains(RoleService.DefaultRoles.PROCESS_ADMINISTRATOR.getRole()));
            adminUser.removeRole(RoleService.DefaultRoles.PROCESS_ADMINISTRATOR.getRole());
            userService.update(adminUser);
            Assert.fail("Must not be able to remove " + RoleService.DefaultRoles.Constants.PROCESS_ADMINISTRATOR_ROLE_ID
                    + " from admin user");
        } catch (IllegalArgumentException ex) {

        }
    }

    @Test
    public void testCannotCreateUsersWithSameLogin() throws Throwable {
        log.info("Start testCannotCreateUsersWithSameLogin");
        // Make sure to be login with user having sufficient permission
        loginAdminUser();
        String name = "user-" + System.currentTimeMillis();
        char[] password = new char[] { 10, 10, 10, 10, 10 };
        final String login = "login-" + System.currentTimeMillis();
        String email = "email-" + System.currentTimeMillis() + "@gmail.com";
        User user = createUser(name, "", false, email, login, password, null);
        Assert.assertNotNull(user);

        try {
            name = "user-" + System.currentTimeMillis();
            createUser(name, "", false, email, login, password, null);
            Assert.fail("Must not be able to create a new user with duplicated login");
        } catch (ServiceException ex) {
            log.trace(ex);
        }
    }

    @Test
    public void testCannotUpdateUsersWithSameLogin() throws Throwable {
        log.info("Start testCannotCreateUsersWithSameLogin");
        // Make sure to be login with user having sufficient permission
        loginAdminUser();
        String name = "user-" + System.currentTimeMillis();
        char[] password = new char[] { 10, 10, 10, 10, 10 };
        final String login = "login-" + System.currentTimeMillis();
        String email = "email-" + System.currentTimeMillis() + "@gmail.com";
        User user = createUser(name, "", false, email, login, password, null);
        Assert.assertNotNull(user);

        User user2 = null;
        try {
            String name2 = "user-" + System.currentTimeMillis();
            final String login2 = "login2-" + System.currentTimeMillis();
            String email2 = "email2-" + System.currentTimeMillis() + "@gmail.com";
            user2 = createUser(name2, "", false, email2, login2, password, null);
            Assert.assertNotNull(user2);
            user2.setLogin(login);
            userService.update(user2);
            Assert.fail("Must not be able to update an existing user with duplicated login");
        } catch (ServiceException ex) {
            log.trace(ex);
        } finally {
            // Delete fake users
            deleteUser(user2.getId());
        }

        // Delete fake users
        deleteUser(user.getId());
    }

    @Test
    public void testFindUser() throws Throwable {
        log.info("Start testFindUser");

        String username = "richard" + System.currentTimeMillis();
        String id = testCreateUser(username, "", false, "", username.toCharArray(), null);
        Assert.assertNotNull(id);
        SearchResult<User> searchResult = userService.search(username, 0, -1);
        // List should not be null
        Assert.assertNotNull(searchResult);
        // List should have one item
        Assert.assertEquals(searchResult.getTotalHits(), 1);
        Set<User> users = searchResult.getItems();

        log.info("User found: " + users.iterator().next().getName());

        // Delete fake users
        deleteUser(id);
    }

    @Test
    public void testCreateUpdateDeleteUser() throws Throwable {
        log.info("Start testCreateUpdateDeleteUser");
        String id = "tst-createupdatedelete-user-" + System.currentTimeMillis() + "@pippo.pippo";
        id = testCreateUser(id, "", false, id, "123456".toCharArray(),
                ImmutableSet.of(RoleService.DefaultRoles.WRITER.getRole(), RoleService.DefaultRoles.READER.getRole()));
        User user = userService.get(id);
        Assert.assertTrue(user.getRoles().contains(RoleService.DefaultRoles.WRITER.getRole()));
        Assert.assertTrue(user.getRoles().contains(RoleService.DefaultRoles.READER.getRole()));
        user.removeRole(RoleService.DefaultRoles.READER.getRole());
        userService.update(user);
        user = userService.get(id);
        Assert.assertTrue(user.getRoles().contains(RoleService.DefaultRoles.WRITER.getRole()));
        Assert.assertFalse(user.getRoles().contains(RoleService.DefaultRoles.READER.getRole()));
        userService.delete(user);
        user = userService.get(id);
        Assert.assertNull(user);
        // Delete fake users
        deleteUser(id);
    }

    @Test
    public void testDeleteUser() throws Throwable {
        log.info("Start testDeleteUser");
        String id = testCreateUser("Richard", "Lead developer", false, "richard@pippo.pippo",
                "123456".toCharArray(), null);
        User user = userService.get(id);
        userService.delete(user);
        user = userService.get(id);
        Assert.assertNull(user);
        // Delete fake users
        deleteUser(id);
    }

    @Test
    public void testListUser() throws Throwable {
        String id1 = testCreateUser("Danilo1", "Lead developer", false, "richard@pippo.pippo", "123456".toCharArray(), null);
        String id2 = testCreateUser("Danilo2", "Mezza calzetta", true, "danilo@pippo.pippo", "123456".toCharArray(), null);
        SearchResult<User> searchResult = userService.search("*", 0, -1);
        Assert.assertNotNull(searchResult);
        Set<User> users = searchResult.getItems();

        int found = 0;
        log.debug(String.format("id1 %s", id1));
        log.debug(String.format("id2 %s", id2));
        for (User user : users) {
            log.debug(String.format("User %s", user.getId()));
            if (id1.equals(user.getId()) || id2.equals(user.getId())) {
                found++;
            }
        }
        Assert.assertEquals(found, 2);

        // Delete fake users
        deleteUser(id1);
        deleteUser(id2);
    }

}
