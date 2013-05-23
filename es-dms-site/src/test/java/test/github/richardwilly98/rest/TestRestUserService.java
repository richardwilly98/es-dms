package test.github.richardwilly98.rest;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.testng.Assert;
import org.testng.annotations.Test;

import test.github.richardwilly98.api.TestUser;

import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.services.UserService;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;

//public class TestRestAuthenticationService extends GuiceAndJerseyTestBase {
public class TestRestUserService extends GuiceAndJettyTestBase {

	public TestRestUserService() throws Exception {
		super();
	}

	@Test
	public void testGetUsers() throws Throwable {
		log.debug("*** testGetUsers ***");
		try {
			ClientResponse response;
			log.debug("Resource: " + resource());
			response = resource().path("users").path("find")
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
			User user2 = getUser(user1.getId());
			Assert.assertEquals(user1.getId(), user2.getId());
			String newName = "user-2-" + System.currentTimeMillis();
			user2.setName(newName);
			User user3 = updateUser(user2);
			Assert.assertEquals(newName, user3.getName());
			deleteUser(user1.getId());
			user2 = getUser(user1.getId());
			Assert.assertNull(user2);
		} catch (Throwable t) {
			log.error("testCreateRetrieveDeleteUpdate fail", t);
			Assert.fail();
		}
	}

	protected User createUser(String login, String password) throws Throwable {
		User user = new TestUser();
		user.setId(login);
		user.setName(login);
		user.setEmail(login);
		user.setPassword(password);
		ClientResponse response = resource().path("users").cookie(adminCookie)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, user);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.CREATED
				.getStatusCode());
		return response.getEntity(User.class);
	}

	protected User updateUser(User user) throws Throwable {
		ClientResponse response = resource().path("users").path(user.getId()).cookie(adminCookie)
				.type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, user);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.OK
				.getStatusCode());
		return response.getEntity(User.class);
	}

	protected User getUser(String id) throws Throwable {
		ClientResponse response = resource().path("users").path(id)
				.cookie(adminCookie).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		log.debug(String.format("status: %s", response.getStatus()));
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return response.getEntity(User.class);
		}
		return null;
	}

	protected void deleteUser(String id) throws Throwable {
		ClientResponse response = resource().path("users").path(id)
				.cookie(adminCookie).type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
	}

	// @Test
	// public void testFindDocuments() throws Throwable {
	// log.debug("*** testFindDocuments ***");
	// ClientConfig clientConfig = new DefaultClientConfig();
	// clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
	// Boolean.TRUE);
	// Client client = Client.create(clientConfig);
	// WebResource webResource = client
	// .resource("http://localhost:8080/api/documents/search/*");
	// ClientResponse response = webResource.get(ClientResponse.class);
	// log.debug("body: " + response.getEntity(String.class));
	// log.debug("status: " + response.getStatus());
	// }

}
