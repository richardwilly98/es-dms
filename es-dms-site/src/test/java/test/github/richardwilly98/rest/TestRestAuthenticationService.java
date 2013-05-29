package test.github.richardwilly98.rest;

import javax.ws.rs.core.Cookie;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Credential;

public class TestRestAuthenticationService extends TestRestUserService {

	public TestRestAuthenticationService() throws Exception {
		super();
	}

	@Test()
	public void testLoginLogout() throws Throwable {
		log.debug("*** testLoginLogout ***");
		try {
			String password = "secret1";
			String login = "user-" + System.currentTimeMillis();
			createUser(login, password);
			boolean rememberMe = true;
			Credential credential = new Credential(login, password, rememberMe);
			Cookie cookie = login(credential);
			Assert.assertNotNull(cookie);
			logout(cookie);
		} catch (Throwable t) {
			log.error("testLoginLogout fail", t);
			Assert.fail();
		}
	}

}
