package com.github.richardwilly98.esdms.client;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.exception.ServiceException;

public class RestAuthenticationServiceClientTest extends RestClientBaseTest {

	@Test
	public void testAdminLoginLogout() {
		log.debug("*** testAdminLoginLogout ***");
		try {
			String token = getRestAuthenticationServiceClient().login(
					new CredentialImpl.Builder().username("admin")
							.password("secret".toCharArray()).build());
			Assert.assertNotNull(token);
			getRestAuthenticationServiceClient().logout(token);
		} catch (ServiceException e) {
			Assert.fail("Fail to login with admin");
		}
		try {
			getRestAuthenticationServiceClient().login(
					new CredentialImpl.Builder().username("kermit")
							.password("secret".toCharArray()).build());
			Assert.fail("Should have failed to login with kermit");
		} catch (ServiceException e) {
		}
	}

	@Test
	public void testKermitLogin() {
		log.debug("*** testKermitLogin ***");
		try {
			getRestAuthenticationServiceClient().login(
					new CredentialImpl.Builder().username("kermit")
							.password("secret".toCharArray()).build());
			Assert.fail("Should have failed to login with kermit");
		} catch (ServiceException e) {
		}
	}

}
