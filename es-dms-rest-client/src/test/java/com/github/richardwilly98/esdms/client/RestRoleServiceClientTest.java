package com.github.richardwilly98.esdms.client;

import java.util.Collection;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;

public class RestRoleServiceClientTest extends RestClientBaseTest {

	RestRoleServiceClientTest() throws Exception {
		super();
	}

	@Test
	public void testFindRolesByType() {
		log.debug("*** testFindRolesByType ***");
		try {
			String token = loginAsAdmin();
			Collection<Role> roles = getRestRoleServiceClient().findRolesByType(token,
					RoleType.SYSTEM);
			Assert.assertNotNull(roles);
			Assert.assertTrue(roles.size() > 0);
		} catch (Throwable t) {
			log.error("testFindRolesByType failed", t);
			Assert.fail("testFindRolesByType failed");
		}
	}

}
