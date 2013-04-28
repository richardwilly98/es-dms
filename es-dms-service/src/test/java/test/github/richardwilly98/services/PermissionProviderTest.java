package test.github.richardwilly98.services;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Permission;

@Guice( modules = ProviderModule.class)
public class PermissionProviderTest extends ProviderTestBase {

	@BeforeSuite
	public void beforeSuite() throws Exception {
	}

	@BeforeClass
	public void setupServer() {
	}

	@AfterClass
	public void closeServer() {
	}

	private String testCreatePermission(String name, String description,
			boolean disabled, Object property) throws Throwable {
		Permission permission = createPermission(name, description, disabled, property);
		Assert.assertNotNull(permission);
		Assert.assertEquals(name, permission.getName());
		Assert.assertEquals(description, permission.getDescription());
		Assert.assertEquals(disabled, permission.isDisabled());
		Assert.assertEquals(property, permission.getProperty());
		return permission.getId();
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
		
		Permission permission = permissionService.get("create-group");
		
		///Assert.assertNotNull(permission);
		if (!(permission == null) )log.info("permission found: " + permission.getName());
		
		permission = permissionService.get("delete-group");
		
		//Assert.assertNotNull(permission);
		if (!(permission == null))log.info("permission found: " + permission.getName());
	}

	@Test
	public void testDeletePermission() throws Throwable {
		log.info("Start testDeletePermission");
		String id = testCreatePermission("write-annotation", "write", false, "annotation");
		Permission permission = permissionService.get(id);
		permissionService.delete(permission);
		permission = permissionService.get(id);
		Assert.assertNull(permission);
	}
}
