package test.github.richardwilly98.esdms.services;

import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.services.RoleService;

public class UserProviderTest extends ProviderTestBase {

	private String testCreateUser(String name, String description,
			boolean disabled, String email, String password, Set<Role> roles)
			throws Throwable {
		User user = createUser(name, description, disabled, email, password,
				roles);
		Assert.assertNotNull(user);
		Assert.assertEquals(name, user.getName());
		Assert.assertEquals(description, user.getDescription());
		Assert.assertEquals(disabled, user.isDisabled());
		Assert.assertEquals(email, user.getEmail());
		if (roles != null) {
			Assert.assertTrue(user.getRoles().equals(roles));
		} else {
			Role defaultRole = roleService.get(RoleService.WRITER_ROLE);
			Assert.assertTrue(user.getRoles().contains(defaultRole));
		}
		return user.getId();
	}

	@Test
	public void testCreateUser() throws Throwable {
		log.info("Start testCreateUser");

		// Make sure to be login with user having sufficient permission
		loginAdminUser();
		testCreateUser("Richard", "Lead developer", false,
				"richard@pippo.pippo", "qwerty", null);
		testCreateUser("Danilo", "Mezza calzetta", true, "danilo@pippo.pippo",
				"123456", null);
	}

	@Test
	public void testFindUser() throws Throwable {
		log.info("Start testFindUser");

		String username = "richard" + System.currentTimeMillis();
		String id = testCreateUser(username, "", false, "", username, null);
		Assert.assertNotNull(id);
		SearchResult<User> searchResult = userService.search(username, 0 , -1);
		// List should not be null
		Assert.assertNotNull(searchResult);
		// List should have one item
		Assert.assertEquals(searchResult.getTotalHits(), 1);
		Set<User> users = searchResult.getItems();
		
		log.info("User found: " + users.iterator().next().getName());

		// user = service.get("Danilo");
		//
		// //Assert.assertNotNull(user);
		// if (!(user == null))log.info("User found: " + user.getName());
	}

	@Test
	public void testDeleteUser() throws Throwable {
		log.info("Start testDeleteUser");
		String id = testCreateUser("Richard", "Lead developer", false,
				"richard@pippo.pippo", "123456", null);
		User user = userService.get(id);
		userService.delete(user);
		user = userService.get(id);
		Assert.assertNull(user);
	}

	@Test
	public void testListUser() throws Throwable {
		String id1 = testCreateUser("Danilo1", "Lead developer", false,
				"richard@pippo.pippo", "123456", null);
		String id2 = testCreateUser("Danilo2", "Mezza calzetta", true,
				"danilo@pippo.pippo", "123456", null);
		SearchResult<User> searchResult = userService.search("*", 0, -1);
		Assert.assertNotNull(searchResult);
		Set<User> users = searchResult.getItems();
		
		int found = 0;
		log.debug(String.format("id1 %s", id1));
		log.debug(String.format("id2 %s", id2));
		for (User user : users) {
			log.debug(String.format("User %s", user.getId()));
			if (id1.equals(user.getId()) || id2.equals(user.getId())) {
				found++;
			}
		}
		Assert.assertEquals(found, 2);
	}
}
