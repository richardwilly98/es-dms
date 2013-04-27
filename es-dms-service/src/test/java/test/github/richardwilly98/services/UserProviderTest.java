package test.github.richardwilly98.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Role;
import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.services.UserService;
import com.google.inject.Inject;

@Guice( modules = ProviderModule.class)
public class UserProviderTest {

	private static Logger log = Logger.getLogger(UserProviderTest.class);

	@BeforeSuite
	public void beforeSuite() throws Exception {
	}

	@BeforeClass
	public void setupServer() {
	}

	@AfterClass
	public void closeServer() {
	}

	@Inject
	UserService service;
	
//	protected UserService getUserProvider() {
//		Injector injector = Guice.createInjector(new ProviderModule());
//		return injector.getInstance(UserProvider.class);
//	}

	private String testCreateUser(String name, String description,
			boolean disabled, String email, List<Role> roles) throws Throwable {
//		UserService provider = getUserProvider();
		User user = new User();
		String id = String.valueOf(System.currentTimeMillis());
		user.setId(id);
		user.setName(name);
		user.setDescription(description);
		user.setDisabled(disabled);
		user.setEmail(email);
		String newId = service.create(user);
		Assert.assertEquals(id, newId);
		User newUser = service.get(newId);
		Assert.assertNotNull(newUser);
		Assert.assertEquals(user.getName(), newUser.getName());
		Assert.assertEquals(user.getDescription(), newUser.getDescription());
		Assert.assertEquals(user.isDisabled(), newUser.isDisabled());
		Assert.assertEquals(user.getEmail(), newUser.getEmail());
		Assert.assertEquals(user.getRoles(), newUser.getRoles());
		return newId;
	}

	@Test
	public void testCreateUser() throws Throwable {
		log.info("Start testCreateUser");
		testCreateUser("Richard", "Lead developer", false,
				"richard@pippo.pippo", null);
		testCreateUser("Danilo", "Mezza calzetta", true, "danilo@pippo.pippo", null);
	}
	
	@Test
	public void testFindUser() throws Throwable {
		log.info("Start testFindUser");

		String username = "richard" + System.currentTimeMillis();
		String id = testCreateUser(username, "", false, "", null);
		Assert.assertNotNull(id);
		List<User> users = service.getList(username);
		// List should not be null
		Assert.assertNotNull(users);
		// List should have one item
		Assert.assertEquals(users.size(), 1);
		log.info("User found: " + users.get(0).getName());
		
//		user = service.get("Danilo");
//		
//		//Assert.assertNotNull(user);
//		if (!(user == null))log.info("User found: " + user.getName());
	}

	@Test
	public void testDeleteUser() throws Throwable {
		log.info("Start testDeleteUser");
		String id = testCreateUser("Richard", "Lead developer", false,
				"richard@pippo.pippo", null);
//		UserService provider = getUserProvider();
		User user = service.get(id);
		service.delete(user);
		user = service.get(id);
		Assert.assertNull(user);
	}
	
//	@Test
//	public void testListUser() throws Throwable {
//		String id1 = testCreateUser("Danilo1", "Lead developer", false,
//				"richard@pippo.pippo", null);
//		String id2 = testCreateUser("Danilo2", "Mezza calzetta", true, "danilo@pippo.pippo", null);
//		UserService provider = getUserProvider();
//		List<User> users = provider.getList("Danilo");
//		Assert.assertNotNull(users);
//		int found = 0;
//		log.debug(String.format("id1 %s", id1));
//		log.debug(String.format("id2 %s", id2));
//		for (User user : users) {
//			log.debug(String.format("User %s", user.getId()));
//			if (id1.equals(user.getId()) || id2.equals(user.getId())) {
//				found++;
//			}
//		}
//		Assert.assertEquals(found, 2);
//	}
}
