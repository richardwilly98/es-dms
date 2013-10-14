package com.github.richardwilly98.esdms.client;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.UserService;

public class RestAuthenticationServiceClientTest extends RestClientBaseTest {

    RestAuthenticationServiceClientTest() throws Exception {
        super();
    }

    @Test
    public void testAdminLoginLogout() {
        log.debug("*** testAdminLoginLogout ***");
        try {
            String token = getRestAuthenticationServiceClient().login(
                    new CredentialImpl.Builder().username(UserService.DEFAULT_ADMIN_LOGIN)
                            .password(UserService.DEFAULT_ADMIN_PASSWORD.toCharArray()).build());
            Assert.assertNotNull(token);
            getRestAuthenticationServiceClient().logout(token);
        } catch (ServiceException e) {
            Assert.fail("Fail to login with admin");
        }
        try {
            getRestAuthenticationServiceClient().login(
                    new CredentialImpl.Builder().username("kermit").password("xxxxxx".toCharArray()).build());
            Assert.fail("Should have failed to login with kermit");
        } catch (ServiceException e) {
        }
    }

    @Test
    public void testKermitLogin() {
        log.debug("*** testKermitLogin ***");
        try {
            getRestAuthenticationServiceClient().login(
                    new CredentialImpl.Builder().username("kermit").password("xxxxxx".toCharArray()).build());
            Assert.fail("Should have failed to login with kermit");
        } catch (ServiceException e) {
        }
    }

    @Test
    public void testLoginValidate() {
        try {
            String login = "user-" + System.currentTimeMillis() + "@yahoo.com";
            createUser(login, login);
            String token = getRestAuthenticationServiceClient().login(
                    new CredentialImpl.Builder().username(login).password(login.toCharArray()).build());
            Assert.assertNotNull(token);
            getRestAuthenticationServiceClient().validate(token);
            getRestAuthenticationServiceClient().logout(token);
        } catch (Throwable t) {
            log.error("testLoginValidate fail", t);
            Assert.fail();
        }
    }
}
