package test.github.richardwilly98.esdms.rest;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.rest.RestItemBaseService;
import com.github.richardwilly98.esdms.rest.RestRoleService;
import com.github.richardwilly98.esdms.rest.RestUserService;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.UserService;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;

//public class TestRestAuthenticationService extends GuiceAndJerseyTestBase {
public class TestRestUserService extends GuiceAndJettyTestBase<UserImpl> {

	public TestRestUserService() throws Exception {
		super();
	}

	private Role getRole(String id) throws Throwable {
		ClientResponse response = resource().path(RestRoleService.ROLES_PATH).path(id)
				.cookie(adminCookie).type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		log.debug(String.format("status: %s", response.getStatus()));
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return response.getEntity(Role.class);
		}
		return null;
	}

	@Test
	public void testGetUsers() throws Throwable {
		log.debug("*** testGetUsers ***");
		try {
			ClientResponse response;
			log.debug("Resource: " + resource());
			response = resource().path(RestUserService.USERS_PATH).path(RestItemBaseService.SEARCH_PATH)
					.path(UserService.DEFAULT_ADMIN_LOGIN).cookie(adminCookie)
					.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			log.debug("status: " + response.getStatus());
			Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
			SearchResult<User> users = response.getEntity(new GenericType<SearchResult<User>>() {
		    });
			Assert.assertNotNull(users);
			Assert.assertTrue(users.getTotalHits() >= 1);
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
			UserImpl user1 = createUser(login, password);
			Assert.assertNotNull(user1);
			Assert.assertNotNull(user1.getRoles());
			Role defaultRole = getRole(RoleService.WRITER_ROLE_ID);
			Assert.assertTrue(user1.getRoles().contains(defaultRole));
			UserImpl user2 = get(user1.getId(), UserImpl.class, RestUserService.USERS_PATH);
			Assert.assertEquals(user1.getName(), user2.getName());
			String newName = "user-2-" + System.currentTimeMillis();
			user2.setName(newName);
			UserImpl user3 = update(user2, UserImpl.class, RestUserService.USERS_PATH);
			Assert.assertEquals(newName, user3.getName());
			delete(user1.getId(), RestUserService.USERS_PATH);
			user2 = get(user1.getId(), UserImpl.class, RestUserService.USERS_PATH);
			Assert.assertNull(user2);
		} catch (Throwable t) {
			log.error("testCreateRetrieveDeleteUpdate fail", t);
			Assert.fail();
		}
	}

	protected UserImpl createUser(String login, String password) throws Throwable {
		log.debug(String.format("*** createUser - %s - %s ***", login, password));
		User user = new UserImpl.Builder().id(login).name(login).email(login).password(password).build();
		String json = mapper.writeValueAsString(user);
		log.trace(json);
		ClientResponse response = resource().path(RestUserService.USERS_PATH).cookie(adminCookie)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, user	);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.CREATED
				.getStatusCode());
		URI uri = response.getLocation();
		Assert.assertNotNull(uri);
		return get(uri, UserImpl.class);
	}

}
