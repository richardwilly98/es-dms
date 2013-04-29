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

	private Permission testCreatePermission(String name, String description,
			boolean disabled) throws Throwable {
		PermissionService provider = getPermissionProvider();
		Permission permission = new Permission();
		String id = name; //String.valueOf(System.currentTimeMillis());
		permission.setId(id);
		permission.setName(name);
		permission.setDescription(description);
		permission.setDisabled(disabled);
		
		Permission aPermission = provider.create(permission);
		Assert.assertEquals(id, aPermission.getId());
		
		Permission newPermission = provider.get(aPermission.getId());
		Assert.assertNotNull(newPermission);
		Assert.assertEquals(permission.getName(), newPermission.getName());
		Assert.assertEquals(permission.getDescription(), newPermission.getDescription());
		Assert.assertEquals(permission.isDisabled(), newPermission.isDisabled());
		
		return newPermission;
	}

	@Test
	public void testCreatePermission() throws Throwable {
		log.info("Start testCreatePermission");
		//content permissions
		testCreatePermission("profile:write", "write, create and edit document metadata", false);
		testCreatePermission("content:write", "write, create and edit document content", true);
		testCreatePermission("profile:read", "read", true);
		testCreatePermission("content:read", "read", true);
		testCreatePermission("access:read", "access control read", true);
		testCreatePermission("access:write", "access control write", true);
		testCreatePermission("profile:todelete", "mark profile as to delete", true);
		testCreatePermission("content:todelete", "mark content as to delete", true);
		testCreatePermission("profile:delete", "delete", true);
		testCreatePermission("content:delete", "delete", true);
		//user permissions
		testCreatePermission("user:create", "create", true);
		testCreatePermission("user:update", "user data update", true);
		testCreatePermission("user:add", "user access update", true);
		testCreatePermission("user:remove", "user access update", true);
		testCreatePermission("user:disable", "user access update", true);
		testCreatePermission("user:delete", "delete", true);
		//group permissions
		testCreatePermission("group:create", "create group", false);
		testCreatePermission("group:update", "group data update", true);
		testCreatePermission("group:add", "group access update", true);
		testCreatePermission("group:remove", "groupaccess update", true);
		testCreatePermission("group:disable", "group access update", true);
		testCreatePermission("group:delete", "delete group", false);
		
		testCreatePermission("group:add-user", "add user to group", false);
		testCreatePermission("group:remove-user", "remove user from group", false);
	}
	
	@Test
	public void testFindPermission() throws Throwable {
		log.info("Start testFindPermission");
		
		Permission p = testCreatePermission("group:add-user", "add user to group", false);
		p = testCreatePermission("group:remove-user", "remove user from group", false);
		
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
		Permission p = testCreatePermission("write-annotation", "write", false);
		PermissionService provider = getPermissionProvider();
		Permission permission = provider.get(p.getId());
		provider.delete(permission);
		permission = provider.get(p.getId());
		Assert.assertNull(permission);
	}
}
