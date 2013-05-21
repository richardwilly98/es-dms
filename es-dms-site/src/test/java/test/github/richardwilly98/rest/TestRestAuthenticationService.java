package test.github.richardwilly98.rest;

import javax.ws.rs.core.MediaType;

import org.testng.Assert;
import org.testng.annotations.Test;

import test.github.richardwilly98.web.TestUser;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.User;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

public class TestRestAuthenticationService extends GuiceAndJerseyTestBase {

	public TestRestAuthenticationService() throws Exception {
		super();
	}

	@Test
	public void testLogin() throws Throwable {
		log.debug("*** testLogin ***");
		try {
			String password = "secret1";
			String login = "user1@gmail.com";
			log.debug("Resource: " + resource());
			User user = new TestUser();
			user.setId(login);
			user.setName(login);
			user.setEmail(login);
			user.setPassword(password);
			ClientResponse response = resource()
					.path("users")
					.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class, mapper.writeValueAsString(user));
			log.debug("body: " + response.getEntity(String.class));
			log.debug("status: " + response.getStatus());
			Assert.assertTrue(response.getStatus() == Status.CREATED
					.getStatusCode());
			WebResource webResource = resource().path("auth").path("login");
			boolean rememberMe = true;
			Credential credential = new Credential(login, password, rememberMe);
			response = webResource.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class,
							mapper.writeValueAsString(credential));
			log.debug("body: " + response.getEntity(String.class));
			log.debug("status: " + response.getStatus());
			Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
		} catch (Throwable t) {
			log.error("testLogin fail", t);
			Assert.fail();
		}
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
