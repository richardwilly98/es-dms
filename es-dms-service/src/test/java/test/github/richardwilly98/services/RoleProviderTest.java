package test.github.richardwilly98.services;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.Role;
import com.github.richardwilly98.api.services.RoleService;
import com.github.richardwilly98.services.RoleProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test
public class RoleProviderTest {

	private static Logger log = Logger.getLogger(RoleProviderTest.class);

	@BeforeSuite
	public void beforeSuite() throws Exception {
	}

	@BeforeClass
	public void setupServer() {
	}

	@AfterClass
	public void closeServer() {
	}

	protected RoleService getRoleProvider() {
		Injector injector = Guice.createInjector(new ProviderModule());
		return injector.getInstance(RoleProvider.class);
	}

	private String testCreateRole(String name, String description, Set<Permission> permissions, boolean disabled) throws Throwable {
		RoleService provider = getRoleProvider();
		Role role = new Role();
		String id = String.valueOf(System.currentTimeMillis());
		role.setId(id);
		role.setName(name);
		role.setDescription(description);
		role.setDisabled(disabled);
		
		String newId = provider.create(role);
		Assert.assertEquals(id, newId);
		
		Role newRole= provider.get(newId);
		Assert.assertNotNull(newRole);
		Assert.assertEquals(role.getName(), newRole.getName());
		Assert.assertEquals(role.getDescription(), newRole.getDescription());
		Assert.assertEquals(role.isDisabled(), newRole.isDisabled());
		
		Assert.assertEquals(role.getPermissions(), newRole.getPermissions());
		return newId;
	}

	@Test
	public void testCreateRole() throws Throwable {
		log.info("Start testCreateRole");
		
		Set<Permission> permissions = new HashSet<Permission>();
		
		testCreateRole("Writer", "writer", permissions, false);
		testCreateRole("Proof-Reader", "reader", permissions, false);
	}
	
//	@Test
//	public void testFindUser() throws Throwable {
//		log.info("Start testFindUser");
//		
//		UserService provider = getUserProvider();
//		User user = provider.get("Richard");
//		
//		Assert.assertNotNull(user);
//		if (!(user == null) )log.info("User found: " + user.getName());
//		
//		user = provider.get("Danilo");
//		
//		//Assert.assertNotNull(user);
//		if (!(user == null))log.info("User found: " + user.getName());
//	}
//	
//	@Test
//	public void testAddUserRole() throws Throwable {
//		log.info("Start testAddUserRole");
//		UserService provider = getUserProvider();
//		User user = provider.get("Richard");
//		
//		
//	}
//
//	@Test
//	public void testDeleteUser() throws Throwable {
//		log.info("Start testDeleteUser");
//		String id = testCreateUser("Richard", "Lead developer", false,
//				"richard@pippo.pippo", null);
//		UserService provider = getUserProvider();
//		User user = provider.get(id);
//		provider.delete(user);
//		user = provider.get(id);
//		Assert.assertNull(user);
//	}
//	
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
