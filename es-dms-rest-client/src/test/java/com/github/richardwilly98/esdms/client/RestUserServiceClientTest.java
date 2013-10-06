package com.github.richardwilly98.esdms.client;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.api.User;

public class RestUserServiceClientTest extends RestClientBaseTest {

	@Test
	public void testFindByUserId() {
		log.debug("*** testFindByUserId ***");
		try {
			String token = loginAsAdmin();
			User user = getRestUserServiceClient().findUserById(token, DEFAULT_ADMIN_LOGIN);
			Assert.assertNotNull(user);
			Assert.assertEquals(user.getLogin(), DEFAULT_ADMIN_LOGIN);
			
			user = getRestUserServiceClient().findUserById(token, "dummy-user-" + System.currentTimeMillis());
			Assert.assertNull(user);
		} catch (Throwable t) {
			log.error("testFindByUserId failed", t);
			Assert.fail("testFindByUserId failed");
		}
	}

	@Test
	public void testFindUserByEmail() {
		log.debug("*** testFindUserByEmail ***");
		try {
			String token = loginAsAdmin();
			User user = getRestUserServiceClient().findUserByEmail(token, DEFAULT_ADMIN_LOGIN);
			Assert.assertNotNull(user);
			Assert.assertEquals(user.getEmail(), DEFAULT_ADMIN_LOGIN);
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
			User user = getRestUserServiceClient().findUserByName(token, DEFAULT_ADMIN_LOGIN);
			Assert.assertNotNull(user);
			Assert.assertEquals(user.getName(), DEFAULT_ADMIN_LOGIN);
		} catch (Throwable t) {
			log.error("testFindUserByName failed", t);
			Assert.fail("testFindUserByName failed");
		}
	}
}
