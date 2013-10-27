package com.github.richardwilly98.esdms.services;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Session;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.services.DocumentService;

public class AuthenticationProviderTest extends ProviderTestBase {

    @Test
    public void testLogin() throws Throwable {
        log.info("Start testLogin");
        for (User user : users) {
            String token = login(user);
            Assert.assertNotNull(token);
            log.trace(String.format("Login %s - token: %s", user.getLogin(), token));
            Session session = getSession(token);
            Assert.assertNotNull(session);
            Assert.assertEquals(token, session.getId());
        }
    }

    @Test
    public void testRolePermission() throws Throwable {
        log.info("Start testRolePermission");
        for (User user : users) {
            String token = login(user);
            Assert.assertNotNull(token);
            for (Role role : roles) {
                log.debug(String.format("Check %s with role %s", user.getLogin(), role.getId()));
                boolean hasRole = hasRole(token, role);
                log.debug(String.format("Has %s role: %s", role.getId(), hasRole));
                if (user.getRoles().contains(role)) {
                    Assert.assertTrue(hasRole);
                    for (Permission permission : role.getPermissions()) {
                        boolean hasPermission = hasPermission(token, permission);
                        log.debug(String.format("Has %s permission: %s", role, hasPermission));
                        if (role.getPermissions().contains(permission)) {
                            String message = String.format("%s should have permission %s from role %s",
                                    user.getLogin(), permission.getId(), role.getId());
                            log.debug(message);
                            Assert.assertTrue(hasPermission, message);
                        } else {
                            String message = String.format("%s should not have permission %s from role %s",
                                    user.getLogin(), permission.getId(), role.getId());
                            log.debug(message);
                            Assert.assertFalse(hasPermission, message);
                        }
                    }
                } else {
                    String message = String.format("%s should not have role %s",
                            user.getLogin(), role.getId());
                    log.debug(message);
                    Assert.assertFalse(hasRole, message);
                }
            }
            log.debug(String.format("Has collaborator role: %s", hasRole(token, collaboratorRole)));
            log.debug(String.format("Has reader role: %s", hasRole(token, readerRole)));
            log.debug(String.format("Has document:create permission: %s", hasPermission(token, DocumentService.DocumentPermissions.CREATE_PERMISSION.getPermission())));
        }
    }

    @Test
    public void testGetSession() throws Throwable {
        log.info("Start testGetSession");
        for (User user : users) {
            String token = login(user);
            Assert.assertNotNull(token);
            Session session = getSession(token);
            Assert.assertNotNull(session);
            Assert.assertNotNull(session.getId());
            Assert.assertNotNull(session.getCreateTime());
            Assert.assertNotNull(session.getLastAccessTime());
            Assert.assertNotNull(session.getUserId());
        }
    }

    @Test
    public void testLogout() throws Throwable {
        log.info("Start testLogout");
        for (User user : users) {
            String token = login(user);
            Assert.assertNotNull(token);
            log.trace(String.format("Login %s - token: %s", user.getLogin(), token));
            logout(token);
            Session session = getSession(token);
            Assert.assertNull(session);
        }
    }
}
