package test.github.richardwilly98.services;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.services.PermissionService;
import com.github.richardwilly98.services.PermissionProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test
public class PermissionProviderTest {

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

	protected PermissionService getPermissionProvider() {
		Injector injector = Guice.createInjector(new ProviderModule());
		return injector.getInstance(PermissionProvider.class);
	}

	private String testCreatePermission(String name, String description,
			boolean disabled, Object property) throws Throwable {
		PermissionService provider = getPermissionProvider();
		Permission permission = new Permission();
		String id = String.valueOf(System.currentTimeMillis());
		permission.setId(id);
		permission.setName(name);
		permission.setDescription(description);
		permission.setDisabled(disabled);
		permission.setProperty(property);
		
		String newId = provider.create(permission);
		Assert.assertEquals(id, newId);
		
		Permission newPermission = provider.get(newId);
		Assert.assertNotNull(newPermission);
		Assert.assertEquals(permission.getName(), newPermission.getName());
		Assert.assertEquals(permission.getDescription(), newPermission.getDescription());
		Assert.assertEquals(permission.isDisabled(), newPermission.isDisabled());
		Assert.assertEquals(permission.getProperty(), newPermission.getProperty());
		
		return newId;
	}

	@Test
	public void testCreatePermission() throws Throwable {
		log.info("Start testCreatePermission");
		testCreatePermission("write-profile", "write", false,"profile");
		testCreatePermission("write-content", "write", true, "content");
		testCreatePermission("read-profile", "read", true, "profile");
		testCreatePermission("read-content", "read", true, "content");
		testCreatePermission("read-access", "read", true, "access");
		testCreatePermission("write-access", "write", true, "access");
		testCreatePermission("delete-profile", "delete", true, "profile");
		testCreatePermission("delete-content", "delete", true, "content");
		
		testCreatePermission("create-user", "create", true, "user-management");
		testCreatePermission("update-user", "update", true, "user-management");
		testCreatePermission("invite-user", "update", true, "user-management");
		testCreatePermission("delete-user", "delete", true, "user-management");
	}
	
	@Test
	public void testFindPermission() throws Throwable {
		log.info("Start testFindPermission");
		
		String id = testCreatePermission("create-group", "create group", false, "group");
		id = testCreatePermission("delete-group", "delete group", false, "group");
		
		PermissionService provider = getPermissionProvider();
		Permission permission = provider.get("create-group");
		
		///Assert.assertNotNull(permission);
		if (!(permission == null) )log.info("permission found: " + permission.getName());
		
		permission = provider.get("delete-group");
		
		//Assert.assertNotNull(permission);
		if (!(permission == null))log.info("permission found: " + permission.getName());
	}

	@Test
	public void testDeletePermission() throws Throwable {
		log.info("Start testDeletePermission");
		String id = testCreatePermission("write-annotation", "write", false, "annotation");
		PermissionService provider = getPermissionProvider();
		Permission permission = provider.get(id);
		provider.delete(permission);
		permission = provider.get(id);
		Assert.assertNull(permission);
	}
}
