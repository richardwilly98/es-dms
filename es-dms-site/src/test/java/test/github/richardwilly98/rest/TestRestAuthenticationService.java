package test.github.richardwilly98.rest;

import javax.ws.rs.core.MediaType;

import org.testng.Assert;
import org.testng.annotations.Test;

import test.github.richardwilly98.api.TestUser;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.services.UserService;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

public class TestRestAuthenticationService extends GuiceAndJerseyTestBase {

	public TestRestAuthenticationService() throws Exception {
		super();
	}

	@Test
	public void testGetUsers() throws Throwable {
		log.debug("*** testGetUsers ***");
		try {
			ClientResponse response;
			log.debug("Resource: " + resource());
			response = resource().path("users")
					.path(UserService.DEFAULT_ADMIN_LOGIN).cookie(adminCookie)
					.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			log.debug("status: " + response.getStatus());
			Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
		} catch (Throwable t) {
			log.error("testGetUsers fail", t);
			Assert.fail();
		}
	}

	@Test()
	public void testLogin() throws Throwable {
		log.debug("*** testLogin ***");
		try {
			String password = "secret1";
			String login = "user1@gmail.com";
			ClientResponse response;
			log.debug("Resource: " + resource());
			createUser(login, password);
			WebResource webResource = resource().path("auth").path("login");
			boolean rememberMe = true;
			Credential credential = new Credential(login, password, rememberMe);
			response = webResource.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class,
							mapper.writeValueAsString(credential));
			log.debug("status: " + response.getStatus());
			Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
		} catch (Throwable t) {
			log.error("testLogin fail", t);
			Assert.fail();
		}
	}

	@Test()
	public void testCreateDelete() throws Throwable {
		log.debug("*** testCreateDelete ***");
		try {
			String password = "secret1";
			String login = "user-create-delete@gmail.com";
			User user = createUser(login, password);
			Assert.assertNotNull(user);
			deleteUser(user.getId());
			user = getUser(user.getId());
			Assert.assertNull(user);
		} catch (Throwable t) {
			log.error("testCreateDelete fail", t);
			Assert.fail();
		}
	}

	private User createUser(String login, String password) throws Throwable {
		User user = new TestUser();
		user.setId(login);
		user.setName(login);
		user.setEmail(login);
		user.setPassword(password);
		ClientResponse response = resource().path("users").cookie(adminCookie)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, mapper.writeValueAsString(user));
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.CREATED
				.getStatusCode());
		return response.getEntity(User.class);
	}

	private User getUser(String id) throws Throwable {
		ClientResponse response = resource().path("users").path(id)
				.cookie(adminCookie).type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		log.debug(String.format("status: %s", response.getStatus()));
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return response.getEntity(User.class);
		}
		return null;
	}

	private void deleteUser(String id) throws Throwable {
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
