package com.github.richardwilly98;

import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.common.collect.ImmutableSet;

public class UserEntityManagerTest extends TestActivitiIdentityServiceBase {

    public UserEntityManagerTest() throws Exception {
        super();
    }

    @Test
    public void testCheckPassword() {
        log.debug("*** checkPassword ***");
        boolean isAuthenticated = identityService.checkPassword("kermit", "kermit");
        Assert.assertFalse(isAuthenticated);
        isAuthenticated = identityService.checkPassword(UserService.DEFAULT_ADMIN_LOGIN, UserService.DEFAULT_ADMIN_PASSWORD);
        Assert.assertTrue(isAuthenticated);
    }

    @Test
    public void testFindAllUsers() {

        log.debug("*** testFindAllUsers ***");
        try {
            UserQuery query = identityService.createUserQuery();
            Assert.assertEquals(query.list().size(), 0);

            String name = "bpm-role-" + System.currentTimeMillis();
            Role role = createGroup(name, RoleType.PROCESS);
            Assert.assertNotNull(role);
            String login1 = "bpm-user-" + System.currentTimeMillis() + "@activiti";
            String login2 = "bpm-user2-" + System.currentTimeMillis() + "@activiti";
            com.github.richardwilly98.esdms.api.User user1 = createUser(login1, login1,
                    ImmutableSet.of(RoleService.DefaultRoles.PROCESS_USER.getRole(), role));
            Assert.assertNotNull(user1);
            com.github.richardwilly98.esdms.api.User user2 = createUser(login2, login2,
                    ImmutableSet.of(RoleService.DefaultRoles.PROCESS_USER.getRole(), role));
            Assert.assertNotNull(user2);
            query = identityService.createUserQuery();
            Assert.assertEquals(query.list().size(), 2);
            deleteGroup(role);
            deleteUser(user1);
            deleteUser(user2);
        } catch (Throwable t) {
            log.error("testFindAllUsers failed", t);
            Assert.fail();
        }
    }

    @Test
    public void testFindUserById() {

        log.debug("*** findUserById ***");
        UserQuery query = identityService.createUserQuery().userId("kermit-" + System.currentTimeMillis());
        Assert.assertEquals(query.list().size(), 0);

        query = identityService.createUserQuery().userId(UserService.DEFAULT_ADMIN_LOGIN);
        Assert.assertTrue(query.list().size() > 0);
        log.debug("query count: " + query.list().size());
        User user = query.singleResult();
        Assert.assertNotNull(user);
        log.debug(user);

        String login = "testbpm-" + System.currentTimeMillis() + "@activiti";
        String password = login;
        try {
            com.github.richardwilly98.esdms.api.User tempUser = createUser(login, password);
            Assert.assertNotNull(tempUser);
            query = identityService.createUserQuery().userId(login);
            Assert.assertEquals(query.list().size(), 1);
            user = query.singleResult();
            Assert.assertNotNull(user);
            Assert.assertEquals(tempUser.getId(), user.getId());
            String token = restAuthenticationService.login(new CredentialImpl.Builder().username(UserService.DEFAULT_ADMIN_LOGIN)
                    .password(UserService.DEFAULT_ADMIN_PASSWORD.toCharArray()).build());
            Assert.assertNotNull(token);
            Assert.assertNotNull(restUserService);
            deleteUser(tempUser);
        } catch (Throwable t) {
            log.error("testFindUserById failed", t);
            Assert.fail();
        }
    }

    @Test
    public void testFindUserByEmail() {
        log.debug("*** testFindUserByEmail ***");
        try {
            String email = "testbpm-" + System.currentTimeMillis() + "@activiti";
            // Find admin email
            UserQuery query = identityService.createUserQuery().userEmail(UserService.DEFAULT_ADMIN_EMAIL);
            Assert.assertEquals(query.list().size(), 1);

            query = identityService.createUserQuery().userEmail(email);
            Assert.assertEquals(query.list().size(), 0);
            com.github.richardwilly98.esdms.api.User tempUser = createUser(email, email);
            Assert.assertNotNull(tempUser);
            Assert.assertNotNull(tempUser.getId());
            query = identityService.createUserQuery().userEmail(email);
            // tempUser does not belong to bpm-user or bpm-admin role
            Assert.assertEquals(query.list().size(), 0);
            deleteUser(tempUser);

            // Get a new email address because the old will generate a conflict
            // (by default index.refresh is 1 sec).
            tempUser = createUser(email, email, ImmutableSet.of(RoleService.DefaultRoles.PROCESS_USER.getRole()));
            Assert.assertNotNull(tempUser);
            query = identityService.createUserQuery().userEmail(email);
            // tempUser belongs to bpm-user role
            Assert.assertEquals(query.list().size(), 1);
            User user = query.singleResult();
            Assert.assertNotNull(user);
            Assert.assertEquals(user.getEmail(), email);
            deleteUser(tempUser);

            tempUser = createUser(email, email, ImmutableSet.of(RoleService.DefaultRoles.PROCESS_ADMINISTRATOR.getRole()));
            Assert.assertNotNull(tempUser);
            query = identityService.createUserQuery().userEmail(email);
            // tempUser belongs to bpm-admin role
            Assert.assertEquals(query.list().size(), 1);
            user = query.singleResult();
            Assert.assertNotNull(user);
            Assert.assertEquals(user.getEmail(), email);
            deleteUser(tempUser);
        } catch (Throwable t) {
            log.error("testFindUserByEmail failed", t);
            Assert.fail();
        }
    }

    @Test
    public void testFindUserByLastName() {
        log.debug("*** testFindUserByLastName ***");
        try {
            String login = "testbpm-" + System.currentTimeMillis() + "@activiti";
            // Find admin login
            UserQuery query = identityService.createUserQuery().userLastName(UserService.DEFAULT_ADMIN_LOGIN);
            Assert.assertEquals(query.list().size(), 1);

            query = identityService.createUserQuery().userEmail(login);
            Assert.assertEquals(query.list().size(), 0);
            com.github.richardwilly98.esdms.api.User tempUser = createUser(login, login);
            Assert.assertNotNull(tempUser);
            Assert.assertNotNull(tempUser.getId());
            query = identityService.createUserQuery().userLastName(tempUser.getName());
            // tempUser does not belong to bpm-user or bpm-admin role
            Assert.assertEquals(query.list().size(), 0);
            deleteUser(tempUser);

            // Get a new email address because the old will generate a conflict
            // (by default index.refresh is 1 sec).
            tempUser = createUser(login, login, ImmutableSet.of(RoleService.DefaultRoles.PROCESS_USER.getRole()));
            Assert.assertNotNull(tempUser);
            query = identityService.createUserQuery().userLastName(tempUser.getName());
            // tempUser belongs to bpm-user role
            Assert.assertEquals(query.list().size(), 1);
            User user = query.singleResult();
            Assert.assertNotNull(user);
            Assert.assertEquals(user.getLastName(), tempUser.getName());
            deleteUser(tempUser);

            tempUser = createUser(login, login, ImmutableSet.of(RoleService.DefaultRoles.PROCESS_ADMINISTRATOR.getRole()));
            Assert.assertNotNull(tempUser);
            query = identityService.createUserQuery().userEmail(tempUser.getName());
            // tempUser belongs to bpm-admin role
            Assert.assertEquals(query.list().size(), 1);
            user = query.singleResult();
            Assert.assertNotNull(user);
            Assert.assertEquals(user.getLastName(), tempUser.getName());
            deleteUser(tempUser);
        } catch (Throwable t) {
            log.error("testFindUserByLastName failed", t);
            Assert.fail();
        }
    }

}
