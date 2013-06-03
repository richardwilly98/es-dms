package test.github.richardwilly98.esdms.services;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.api.ISession;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;

public class AuthenticationProviderTest extends ProviderTestBase {

	@Test
	public void testLogin() throws Throwable {
		log.info("Start testLogin");
		for (User user : users.values()) {
			Credential credential = new CredentialImpl(user.getLogin(),
					user.getPassword());
			String token = authenticationService.login(credential);
			Assert.assertNotNull(token);
			log.trace(String.format("Login %s - token: %s", user.getLogin(),
					token));
			ISession session = authenticationService.get(token);
			Assert.assertNotNull(session);
			Assert.assertEquals(token, session.getId());
		}
	}

	@Test
	public void testRolePermission() throws Throwable {
		log.info("Start testRolePermission");
		for (User user : users.values()) {
			Credential credential = new CredentialImpl(user.getLogin(),
					user.getPassword());
			String token = authenticationService.login(credential);
			Assert.assertNotNull(token);
			for (Role role : roles) {
				boolean hasRole = authenticationService.hasRole(token,
						role.getId());
				log.debug(String.format("Has %s role: %s", role.getId(),
						hasRole));
				if (user.getRoles().contains(role)) {
					Assert.assertTrue(hasRole);
					for (Permission permission : permissions) {
						boolean hasPermission = authenticationService
								.hasPermission(token, permission.getId());
						log.debug(String.format("Has %s permission: %s",
								permission.getId(), hasPermission));
						if (role.getPermissions().contains(permission)) {
							Assert.assertTrue(hasPermission);
						} else {
							Assert.assertFalse(hasPermission);
						}
					}
				} else {
					Assert.assertFalse(hasRole);
				}
			}
			log.debug(String.format("Has collaborator role: %s",
					authenticationService.hasRole(token, "collaborator")));
			log.debug(String.format("Has reader role: %s",
					authenticationService.hasRole(token, "reader")));
			log.debug(String.format("Has document:create permission: %s",
					authenticationService.hasPermission(token,
							"document:create")));
		}
	}

	@Test
	public void testGetSession() throws Throwable {
		log.info("Start testGetSession");
		for (User user : users.values()) {
			Credential credential = new CredentialImpl(user.getLogin(),
					user.getPassword());
			String token = authenticationService.login(credential);
			Assert.assertNotNull(token);
			ISession session = authenticationService.get(token);
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
		for (User user : users.values()) {
			Credential credential = new CredentialImpl(user.getLogin(),
					user.getPassword());
			String token = authenticationService.login(credential);
			Assert.assertNotNull(token);
			log.trace(String.format("Login %s - token: %s", user.getLogin(),
					token));
			authenticationService.logout(token);
			ISession session = authenticationService.get(token);
			Assert.assertNull(session);
		}
	}
}
