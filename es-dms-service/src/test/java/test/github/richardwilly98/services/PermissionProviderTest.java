package test.github.richardwilly98.services;

import org.apache.log4j.Logger;
import org.testng.Assert;
<<<<<<< HEAD
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
=======
>>>>>>> Refactoring tests
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.services.PermissionService;
import com.github.richardwilly98.services.PermissionProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;

<<<<<<< HEAD
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

	private Permission testCreatePermission(String name, String description,
=======
public class PermissionProviderTest extends ProviderTestBase {

	private String testCreatePermission(String name, String description,
>>>>>>> Refactoring tests
			boolean disabled, Object property) throws Throwable {
		PermissionService provider = getPermissionProvider();
		Permission permission = new Permission();
		String id = name; //String.valueOf(System.currentTimeMillis());
		permission.setId(id);
		permission.setName(name);
		permission.setDescription(description);
		permission.setDisabled(disabled);
		permission.setProperty(property);
		
		Permission aPermission = provider.create(permission);
		Assert.assertEquals(id, aPermission.getId());
		
		Permission newPermission = provider.get(aPermission.getId());
		Assert.assertNotNull(newPermission);
		Assert.assertEquals(permission.getName(), newPermission.getName());
		Assert.assertEquals(permission.getDescription(), newPermission.getDescription());
		Assert.assertEquals(permission.isDisabled(), newPermission.isDisabled());
		Assert.assertEquals(permission.getProperty(), newPermission.getProperty());
		
		return newPermission;
	}

	@Test
	public void testCreatePermission() throws Throwable {
		log.info("Start testCreatePermission");
		//content permissions
		testCreatePermission("profile:write", "write, create and edit document metadata", false,"profile");
		testCreatePermission("content:write", "write, create and edit document content", true, "content");
		testCreatePermission("profile:read", "read", true, "profile");
		testCreatePermission("content:read", "read", true, "content");
		testCreatePermission("access:read", "access control read", true, "access");
		testCreatePermission("access:write", "access control write", true, "access");
		testCreatePermission("profile:todelete", "mark profile as to delete", true, "profile");
		testCreatePermission("content:todelete", "mark content as to delete", true, "content");
		testCreatePermission("profile:delete", "delete", true, "profile");
		testCreatePermission("content:delete", "delete", true, "content");
		//user permissions
		testCreatePermission("user:create", "create", true, "user-management");
		testCreatePermission("user:update", "user data update", true, "user-management");
		testCreatePermission("user:add", "user access update", true, "user-management");
		testCreatePermission("user:remove", "user access update", true, "user-management");
		testCreatePermission("user:disable", "user access update", true, "user-management");
		testCreatePermission("user:delete", "delete", true, "user-management");
		//group permissions
		testCreatePermission("group:create", "create group", false, "group");
		testCreatePermission("group:update", "group data update", true, "group-management");
		testCreatePermission("group:add", "group access update", true, "group-management");
		testCreatePermission("group:remove", "groupaccess update", true, "group-management");
		testCreatePermission("group:disable", "group access update", true, "group-management");
		testCreatePermission("group:delete", "delete group", false, "group");
		
		testCreatePermission("group:add-user", "add user to group", false, "group-management");
		testCreatePermission("group:remove-user", "remove user from group", false, "group-management");
	}
	
	@Test
	public void testFindPermission() throws Throwable {
		log.info("Start testFindPermission");
		
		Permission p = testCreatePermission("group:add-user", "add user to group", false, "group-management");
		p = testCreatePermission("group:remove-user", "remove user from group", false, "group-management");
		
		PermissionService provider = getPermissionProvider();
		Permission permission = provider.get("group:add-user");
		
		Assert.assertNotNull(permission);
		if (!(permission == null) )log.info("permission found: " + permission.getName());
		
		permission = provider.get("group:remove-user");
		
		Assert.assertNotNull(permission);
		if (!(permission == null))log.info("permission found: " + permission.getName());
	}

	@Test
	public void testDeletePermission() throws Throwable {
		log.info("Start testDeletePermission");
		Permission p = testCreatePermission("write-annotation", "write", false, "annotation");
		PermissionService provider = getPermissionProvider();
		Permission permission = provider.get(p.getId());
		provider.delete(permission);
		permission = provider.get(p.getId());
		Assert.assertNull(permission);
	}
}
