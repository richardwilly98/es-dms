package test.github.richardwilly98.services;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Permission;

public class PermissionProviderTest extends ProviderTestBase {

	private String testCreatePermission(String name, String description,
			boolean disabled, Object property) throws Throwable {
		Permission permission = createPermission(name, description,
				disabled, property);
//				Permission permission = new Permission();
//		String id = name; //String.valueOf(System.currentTimeMillis());
//		permission.setId(id);
//		permission.setName(name);
//		permission.setDescription(description);
//		permission.setDisabled(disabled);
//		permission.setProperty(property);
		Assert.assertNotNull(permission);
		
//		Permission aPermission = permissionService.create(permission);
//		Assert.assertEquals(id, aPermission.getId());
		
//		Permission newPermission = permissionService.get(aPermission.getId());
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
		
		Permission p = createPermission("group:add-user", "add user to group", false, "group-management");
		p = createPermission("group:remove-user", "remove user from group", false, "group-management");
		
		Permission permission = permissionService.get("group:add-user");
		
		Assert.assertNotNull(permission);
		if (!(permission == null) )log.info("permission found: " + permission.getName());
		
		permission = permissionService.get("group:remove-user");
		
		Assert.assertNotNull(permission);
		if (!(permission == null))log.info("permission found: " + permission.getName());
	}

	@Test
	public void testDeletePermission() throws Throwable {
		log.info("Start testDeletePermission");
		Permission p = createPermission("write-annotation", "write", false, "annotation");
		Permission permission = permissionService.get(p.getId());
		permissionService.delete(permission);
		permission = permissionService.get(p.getId());
		Assert.assertNull(permission);
	}
}
