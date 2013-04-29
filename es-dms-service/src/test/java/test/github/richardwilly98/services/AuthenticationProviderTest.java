package test.github.richardwilly98.services;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.User;

public class AuthenticationProviderTest extends ProviderTestBase {

	@Test
	public void testLogin() throws Throwable {
		log.info("Start testLogin");
		for (User user : users.values()) {
			Credential credential = new Credential(user.getId(), user.getPassword());
			String token = authenticationService.login(credential);
			Assert.assertNotNull(token);
		}
	}
	
}
