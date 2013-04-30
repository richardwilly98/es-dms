package test.github.richardwilly98.services;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.ISession;
import com.github.richardwilly98.api.User;

public class AuthenticationProviderTest extends ProviderTestBase {

	@Test
	public void testLogin() throws Throwable {
	    
	    log.info("Start testLogin");
		for (User user : users.values()) {
			Credential credential = new Credential(user.getLogin(), user.getPassword());
			String token = authenticationService.login(credential);
			Assert.assertNotNull(token);
			Subject currentUser = SecurityUtils.getSubject();
			Assert.assertNotNull(currentUser);
			log.trace(String.format("Login %s - token: %s", user.getLogin(), token));
			ISession session = authenticationService.get(token);
			Assert.assertNotNull(session);
			Assert.assertEquals(token, session.getId());
			currentUser = SecurityUtils.getSubject();
			log.trace(String.format("before logout - principal: %s", currentUser));
			log.trace(String.format("after logout - principal: %s", currentUser));
		}
	}
	
	@Test(enabled=false)
	public void testGetSession() throws Throwable {
		log.info("Start testGetSession");
		for (User user : users.values()) {
			Credential credential = new Credential(user.getLogin(), user.getPassword());
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
			Credential credential = new Credential(user.getLogin(), user.getPassword());
			String token = authenticationService.login(credential);
			Assert.assertNotNull(token);
			log.trace(String.format("Login %s - token: %s", user.getLogin(), token));
			authenticationService.logout(token);
		}
	}
}
