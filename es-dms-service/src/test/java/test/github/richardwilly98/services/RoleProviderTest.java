package test.github.richardwilly98.services;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.Role;
import com.github.richardwilly98.api.services.RoleService;
import com.google.inject.Inject;

@Guice( modules = ProviderModule.class)
public class RoleProviderTest extends ProviderTestBase {

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
	RoleService roleService;

	private String testCreateRole(String name, String description, /*Set<Permission> permissions,*/Map<String, Permission> permissions, boolean disabled) throws Throwable {
		Role role = createRole(name, description, disabled, permissions);
		Assert.assertNotNull(role);
		Assert.assertEquals(name, role.getName());
		Assert.assertEquals(description, role.getDescription());
		Assert.assertEquals(disabled, role.isDisabled());
		Assert.assertEquals(permissions, role.getPermissions());
		return role.getId();
	}

	@Test
	public void testCreateRole() throws Throwable {
		log.info("Start testCreateRole");
		
//		Set<Permission> permissions = new HashSet<Permission>();
		Map<String, Permission> permissions = new HashMap<String, Permission>();
		
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
