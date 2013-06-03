package test.github.richardwilly98.esdms.api;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.User;

public class UserSerializationTest {

	private static Logger log = Logger.getLogger(UserSerializationTest.class);
	
	private static final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testSerializeDeserializeUser() throws Throwable {
		log.debug("*** testSerializeDeserializeUser ***");
		User user = new UserImpl();
		String id = "user-" + System.currentTimeMillis();
		user.setId(id);
		String name = id;
		user.setName(name);
		String email = id + "@gmail.com";
		user.setEmail(email);
		String password = "secret";
		user.setPassword(password);
		log.debug(user);
		String json = mapper.writeValueAsString(user);
		log.debug(json);
		Assert.assertNotNull(json);
		User user2 = mapper.readValue(json, User.class);
		log.debug(user2);
		Assert.assertEquals(user.getId(), user2.getId());
	}
}
