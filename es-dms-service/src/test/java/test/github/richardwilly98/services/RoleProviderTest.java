package test.github.richardwilly98.services;

import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.Role;
import com.github.richardwilly98.api.User;

public class RoleProviderTest extends ProviderTestBase {

	private String testCreateRole(String name, String description, Set<Permission> permissions, boolean disabled) throws Throwable {
		
		Role role = createRole(name, description, disabled, permissions);
		
		log.trace("role created: " + role.getId());
		Assert.assertNotNull(role);
		log.trace("role name: " + role.getName());
		Assert.assertEquals(name, role.getName());
		log.trace("role description: " + role.getDescription());
		Assert.assertEquals(description, role.getDescription());
		log.trace("role disabled: " + role.isDisabled());
		Assert.assertEquals(disabled, role.isDisabled());

		return role.getId();
	}
	
	@Test
	public void testCreateRole() throws Throwable {
		log.info("Start testCreateRole");
		
		Set<Permission> permissions = new HashSet<Permission>();
		
		permissions.add(createPermission("profile:read", "profile:read", true));
		permissions.add(createPermission("content:read", "content:read", true));
		permissions.add(createPermission("annotation:read", "annotation:read", true));
		permissions.add(createPermission("annotation:write", "annotation:write", true));
		permissions.add(createPermission("comment:read", "comment:read", true));
		permissions.add(createPermission("comment:write", "comment:write", true));
		permissions.add(createPermission("content:todelete", "content:todelete", true));
		testCreateRole("Proof-Reader", "reader", permissions, false);
		log.info("Proof-Reader permissions count: " + permissions.size());
		Assert.assertEquals(permissions.size(), 7);
		permissions.add(createPermission("profile:write", "profile:write", true));
		permissions.add(createPermission("content:write", "content:write", true));
		permissions.add(createPermission("content:add", "content:add", true));
		permissions.add(createPermission("content:remove", "content:remove", true));
		permissions.add(createPermission("profile:todelete", "profile:todelete", true));
		testCreateRole("Writer", "writer", permissions, false);
		log.info("Writer permissions count: " + permissions.size());
		Assert.assertEquals(permissions.size(), 12);
		permissions.add(createPermission("user:add", "user:add", true));
		permissions.add(createPermission("user:remove", "user:remove", true));
		permissions.add(createPermission("group:add", "group:add", true));
		permissions.add(createPermission("group:remove", "group:remove", true));
		permissions.add(createPermission("role:add", "role:add", true));
		permissions.add(createPermission("role:remove", "role:remove", true));
		testCreateRole("Editor", "Editor", permissions, false);
		log.info("Editor permissions count: " + permissions.size());
		Assert.assertEquals(permissions.size(), 18);
		permissions.add(createPermission("milestone:add", "milestone:add", true));
		permissions.add(createPermission("milestone:remove", "milestone:remove", true));
		permissions.add(createPermission("task:assign", "task:assign", true));
		testCreateRole("Coordinator", "coordinator", permissions, false);
		log.info("Coordinator permissions count: " + permissions.size());
		Assert.assertEquals(permissions.size(), 21);
		
		log.info("Start testCreateRole completed!!");
	}
	
	@Test
	public void testFindRole() throws Throwable {
		log.info("Start testFindRole");
		
		Set<Permission> permissions = new HashSet<Permission>();
		
		permissions.add(createPermission("profile:read", "profile:read", true));
		permissions.add(createPermission("content:read", "content:read", true));
		permissions.add(createPermission("annotation:read", "annotation:read", true));
		permissions.add(createPermission("annotation:write", "annotation:write", true));
		permissions.add(createPermission("comment:read", "comment:read", true));
		permissions.add(createPermission("comment:write", "comment:write", true));
		permissions.add(createPermission("content:todelete", "content:todelete", true));
		testCreateRole("Proof-Reader", "reader", permissions, false);
		log.info("Proof-Reader permissions count: " + permissions.size());
		
		Role role = roleService.get("Proof-Reader");
		
		Assert.assertNotNull(role);
		if (!(role == null) )log.info("Role found: " + role.getName());
		
		permissions.add(createPermission("profile:write", "profile:write", true));
		permissions.add(createPermission("content:write", "content:write", true));
		permissions.add(createPermission("content:add", "content:add", true));
		permissions.add(createPermission("content:remove", "content:remove", true));
		permissions.add(createPermission("profile:todelete", "profile:todelete", true));
		testCreateRole("writer", "writer", permissions, false);
		log.info("Writer permissions count: " + permissions.size());
		
		role = roleService.get("Writer");
		
		Assert.assertNotNull(role);
		if (!(role == null))log.info("Role found: " + role.getName());
	}
	
	@Test
	public void testAddRoletoUser() throws Throwable {
		log.info("Start testAddRoletoUser");
		User user = userService.get("richard.louapre@gmail.com");
		if (user == null) log.error("Failed to retrieve user richard.louapre@gmail.com!!");
		
		Role role = roleService.get("collaborator");
		if (role == null) log.error("Failed to retrieve role collaborator!!");
		
	}

}
