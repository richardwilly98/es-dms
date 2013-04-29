package test.github.richardwilly98.services;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.Session;
import com.github.richardwilly98.api.User;

public class AuthenticationProviderTest extends ProviderTestBase {

	@Test
	public void testLogin() throws Throwable {
		log.info("Start testLogin");
		for (User user : users.values()) {
			Credential credential = new Credential(user.getId(), user.getPassword());
			String token = authenticationService.login(credential);
			Assert.assertNotNull(token);
			log.trace(String.format("Token: %s", token));
			Session session = authenticationService.get(token);
			Assert.assertNotNull(session);
			Assert.assertEquals(token, session.getId());
		}
	}
	
	@Test
	public void testGetSession() throws Throwable {
		log.info("Start testGetSession");
		for (User user : users.values()) {
			Credential credential = new Credential(user.getId(), user.getPassword());
			String token = authenticationService.login(credential);
			Assert.assertNotNull(token);
			Session session = authenticationService.get(token);
			Assert.assertNotNull(session);
			Assert.assertNotNull(session.getId());
			Assert.assertNotNull(session.getCreateTime());
			Assert.assertNotNull(session.getLastAccessedTime());
			Assert.assertNotNull(session.getUserId());
		}
	}
	
	@Test
	public void testLogout() throws Throwable {
		log.info("Start testLogout");
		for (User user : users.values()) {
			Credential credential = new Credential(user.getId(), user.getPassword());
			String token = authenticationService.login(credential);
			Assert.assertNotNull(token);
			log.trace(String.format("Token: %s", token));
			authenticationService.logout(token);
		}
	}
}
