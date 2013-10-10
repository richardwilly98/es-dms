package com.github.richardwilly98.esdms.client;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.services.UserService;

public class RestUserServiceClientTest extends RestClientBaseTest {

    RestUserServiceClientTest() throws Exception {
        super();
    }

    @Test
    public void testFindByUserId() {
        log.debug("*** testFindByUserId ***");
        try {
            String token = loginAsAdmin();
            User user = getRestUserServiceClient().findUserById(token, UserService.DEFAULT_ADMIN_LOGIN);
            Assert.assertNotNull(user);
            Assert.assertEquals(user.getLogin(), UserService.DEFAULT_ADMIN_LOGIN);

            user = getRestUserServiceClient().findUserById(token, "dummy-user-" + System.currentTimeMillis());
            Assert.assertNull(user);
        } catch (Throwable t) {
            log.error("testFindByUserId failed", t);
            Assert.fail("testFindByUserId failed");
        }
    }

    @Test
    public void testFindByUserLogin() {
        log.debug("*** testFindByUserLogin ***");
        try {
            String token = loginAsAdmin();
            User user = getRestUserServiceClient().findUserByLogin(token, UserService.DEFAULT_ADMIN_LOGIN);
            Assert.assertNotNull(user);
            Assert.assertEquals(user.getLogin(), UserService.DEFAULT_ADMIN_LOGIN);

            user = getRestUserServiceClient().findUserByLogin(token, "dummy-user-" + System.currentTimeMillis());
            Assert.assertNull(user);
        } catch (Throwable t) {
            log.error("testFindByUserLogin failed", t);
            Assert.fail("testFindByUserLogin failed");
        }
    }

    @Test
    public void testFindUserByEmail() {
        log.debug("*** testFindUserByEmail ***");
        try {
            String token = loginAsAdmin();
            User user = getRestUserServiceClient().findUserByEmail(token, UserService.DEFAULT_ADMIN_EMAIL);
            Assert.assertNotNull(user);
            Assert.assertEquals(user.getEmail(), UserService.DEFAULT_ADMIN_EMAIL);
        } catch (Throwable t) {
            log.error("testFindUserByEmail failed", t);
            Assert.fail("testFindUserByEmail failed");
        }
    }

    @Test
    public void testFindUserByName() {
        log.debug("*** testFindUserByName ***");
        try {
            String token = loginAsAdmin();
            User user = getRestUserServiceClient().findUserByName(token, UserService.DEFAULT_ADMIN_LOGIN);
            Assert.assertNotNull(user);
            Assert.assertEquals(user.getName(), UserService.DEFAULT_ADMIN_LOGIN);
        } catch (Throwable t) {
            log.error("testFindUserByName failed", t);
            Assert.fail("testFindUserByName failed");
        }
    }

    @Test
    public void testCreateDeleteUser() {
        log.debug("*** testCreateDeleteUser ***");
        try {
            String token = loginAsAdmin();
            String id = "user-" + System.currentTimeMillis() + "@gmail.com";
            char[] password = new char[] { 11, 12, 13, 14, 15, 16, 17 };
            User user = new UserImpl.Builder().id(id).name(id).email(id).login(id).password(password).build();
            Assert.assertNotNull(user);
            User user2 = getRestUserServiceClient().create(token, user);
            Assert.assertNotNull(user2);
            Assert.assertEquals(user.getId(), user2.getId());
            Assert.assertEquals(user.getName(), user2.getName());
            Assert.assertEquals(user.getLogin(), user2.getLogin());
            Assert.assertEquals(user.getEmail(), user2.getEmail());

            User user3 = getRestUserServiceClient().findUserById(token, id);
            Assert.assertNotNull(user2);
            Assert.assertEquals(user2, user3);

            getRestUserServiceClient().delete(token, user3);
            user3 = getRestUserServiceClient().findUserById(token, id);
            Assert.assertNull(user3);

        } catch (Throwable t) {
            log.error("testCreateDeleteUser failed", t);
            Assert.fail("testCreateDeleteUser failed");
        }
    }
}
