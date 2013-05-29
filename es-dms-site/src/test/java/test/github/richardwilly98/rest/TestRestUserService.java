package test.github.richardwilly98.rest;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.testng.Assert;
import org.testng.annotations.Test;

import test.github.richardwilly98.api.TestUser;

import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.services.UserService;
import com.github.richardwilly98.rest.RestServiceBase;
import com.github.richardwilly98.rest.RestUserService;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;

//public class TestRestAuthenticationService extends GuiceAndJerseyTestBase {
public class TestRestUserService extends GuiceAndJettyTestBase<User> {

	public TestRestUserService() throws Exception {
		super();
	}

	@Test
	public void testGetUsers() throws Throwable {
		log.debug("*** testGetUsers ***");
		try {
			ClientResponse response;
			log.debug("Resource: " + resource());
			response = resource().path(RestUserService.USERS_PATH).path(RestServiceBase.FIND_PATH)
					.path(UserService.DEFAULT_ADMIN_LOGIN).cookie(adminCookie)
					.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			log.debug("status: " + response.getStatus());
			Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
			List<User> users = response.getEntity(new GenericType<List<User>>() {
		    });
			Assert.assertNotNull(users);
			Assert.assertTrue(users.size() >= 1);
		} catch (Throwable t) {
			log.error("testGetUsers fail", t);
			Assert.fail();
		}
	}

	@Test
	public void testCreateRetrieveDeleteUpdate() throws Throwable {
		log.debug("*** testCreateRetrieveDeleteUpdate ***");
		try {
			String password = "secret1";
			String login = "user-" + System.currentTimeMillis();
			User user1 = createUser(login, password);
			Assert.assertNotNull(user1);
			User user2 = get(user1.getId(), User.class, RestUserService.USERS_PATH);
			Assert.assertEquals(user1.getName(), user2.getName());
			String newName = "user-2-" + System.currentTimeMillis();
			user2.setName(newName);
			User user3 = update(user2, User.class, RestUserService.USERS_PATH);
			Assert.assertEquals(newName, user3.getName());
			delete(user1.getId(), RestUserService.USERS_PATH);
			user2 = get(user1.getId(), User.class, RestUserService.USERS_PATH);
			Assert.assertNull(user2);
		} catch (Throwable t) {
			log.error("testCreateRetrieveDeleteUpdate fail", t);
			Assert.fail();
		}
	}

	protected User createUser(String login, String password) throws Throwable {
		log.debug(String.format("*** createUser - %s - %s ***", login, password));
		TestUser user = new TestUser();
		user.setId(login);
		user.setName(login);
		user.setEmail(login);
		user.setPassword(password);
		String json = mapper.writeValueAsString(user);
		log.trace(json);
		ClientResponse response = resource().path(RestUserService.USERS_PATH).cookie(adminCookie)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, user);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.CREATED
				.getStatusCode());
		URI uri = response.getLocation();
		Assert.assertNotNull(uri);
		return get(uri, User.class);
	}

}
