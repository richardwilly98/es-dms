package test.github.richardwilly98.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import test.github.richardwilly98.inject.ProviderModule;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.Role;
import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.AuthenticationService;
import com.github.richardwilly98.api.services.DocumentService;
import com.github.richardwilly98.api.services.PermissionService;
import com.github.richardwilly98.api.services.RoleService;
import com.github.richardwilly98.api.services.UserService;
import com.google.inject.Inject;

/*
 * Base class for all test providers
 */
@Test
@Guice(modules = ProviderModule.class)
abstract class ProviderTestBase {

	Logger log = Logger.getLogger(this.getClass());
	final static Credential adminCredential = new Credential("admin", "secret");
	final static Map<String, User> users = new HashMap<String, User>();
	final static Set<Permission> permissions = new HashSet<Permission>();
	final static Set<Role> roles = new HashSet<Role>();
	static String adminToken;

	static {
		Permission permissionCreateDocument = new Permission("document:create");
//		permissionCreateDocument.setId("document:create");
//		permissionCreateDocument.setName(permissionCreateDocument.getId());
//		permissionCreateDocument.setDisabled(false);
		permissionCreateDocument.setDescription("Create document");
		permissions.add(permissionCreateDocument);
		
		Permission permissionDeleteDocument = new Permission("document:delete");
//		permissionDeleteDocument.setId("document:delete");
//		permissionDeleteDocument.setName(permissionDeleteDocument.getId());
//		permissionDeleteDocument.setDisabled(false);
		permissionDeleteDocument.setDescription("Delete document");
		permissions.add(permissionDeleteDocument);
		
		Permission permissionReadDocument = new Permission("document:read");
//		permissionReadDocument.setId("document:read");
//		permissionReadDocument.setName(permissionReadDocument.getId());
//		permissionReadDocument.setDisabled(false);
		permissionReadDocument.setDescription("Read document");
		permissions.add(permissionReadDocument);

		Role collaboratorRole = new Role();
		collaboratorRole.setId("collaborator");
		collaboratorRole.setName(collaboratorRole.getId());
		collaboratorRole.setDescription("Collaborator");
		collaboratorRole.setDisabled(false);
		collaboratorRole.setPermissions(permissions);
		roles.add(collaboratorRole);
		
		Role readerRole = new Role();
		readerRole.setId("reader");
		readerRole.setName(readerRole.getId());
		readerRole.setDescription("Reader");
		readerRole.setDisabled(false);
		Set<Permission> ps = new HashSet<Permission>();
		ps.add(permissionReadDocument);
		readerRole.setPermissions(ps);
		roles.add(readerRole);

		User user = new User();
		user.setId("richard.louapre@gmail.com");
		user.setEmail(user.getId());
		user.setName("Richard");
		user.setDisabled(false);
		user.setCity("Jersey City");
		user.setPassword("secret");
		user.addRole(readerRole);
		
		users.put(user.getLogin(), user);
		user = new User();
		user.setId("danilo.sandron@gmail.com");
		user.setEmail(user.getId());
		user.setName("Danilo");
		user.setDisabled(false);
		user.setCity("Bankok");
		user.setPassword("segreto");
		user.addRole(collaboratorRole);
		users.put(user.getLogin(), user);
	}

	@Inject Client client;
	
	@Inject
	AuthenticationService authenticationService;

	@Inject
	UserService userService;

	@Inject
	DocumentService documentService;

	@Inject
	RoleService roleService;

	@Inject
	PermissionService permissionService;

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite() {
		log.info("** beforeSuite **");
		createAdminUser();
	    createPermissions();
		createRoles();
		createUsers();
	}

	private void createAdminUser() {
		try {
			adminToken = authenticationService.login(adminCredential);
		} catch (ServiceException ex) {
			Assert.fail("createAdminUser failed", ex);
		}
	}
	private void createUsers() {
		try {
			for (User user : users.values()) {
				String password = user.getPassword();
				userService.create(user);
				user.setPassword(password);
			}
		} catch (ServiceException ex) {
			Assert.fail("createUsers failed", ex);
		}
	}

	private void createRoles() {
		try {
			for (Role role : roles) {
				roleService.create(role);
			}
		} catch (ServiceException ex) {
			Assert.fail("createRoles failed", ex);
		}
	}

	private void createPermissions() {
		try {
			for (Permission permission : permissions) {
				permissionService.create(permission);
			}
		} catch (ServiceException ex) {
			Assert.fail("createPermissions failed", ex);
		}
	}

	@BeforeClass
	public void setupServer() {
		log.info("** setupServer **");
	}

	@AfterClass
	public void closeServer() {
		log.info("** closeServer **");
	}

	Permission createPermission(String name, String description,
			boolean disabled) throws ServiceException {
		Assert.assertTrue(! (name == null || name.isEmpty()));

		Permission permission = new Permission();
		String id = String.valueOf(name);
		permission.setId(id);
		permission.setName(name);
		permission.setDescription(description);
		permission.setDisabled(disabled);
		try {
			Permission newPermission = permissionService.create(permission);
			Assert.assertNotNull(newPermission);
			Assert.assertEquals(id, newPermission.getId());
			return newPermission;
		} catch (ServiceException e) {
			log.error("createPermission failed", e);
			throw e;
		}
	}

	User createUser(String name, String description, boolean disabled,
			String email, String password, Set<Role> roles)
			throws ServiceException {
		User user = new User();
		String id = String.valueOf(email);
		user.setId(id);
		user.setName(name);
		user.setDescription(description);
		user.setDisabled(disabled);
		user.setEmail(email);
		user.setPassword(password);
		try {
			User newUser = userService.create(user);
			Assert.assertNotNull(newUser);
			Assert.assertEquals(id, newUser.getId());
			return newUser;
		} catch (ServiceException e) {
			log.error("createUser failed", e);
			throw e;
		}
	}

	Role createRole(String name, String description, /*
													 * Set<Permission>
													 * permissions,
													 */
			boolean disabled, Set<Permission> permissions)
			throws ServiceException {
		log.trace("Preparing to create permission: " + name);
		Assert.assertTrue(!(name == null || name.isEmpty()));
		Role role = new Role();
		String id = String.valueOf(name);
		role.setId(id);
		role.setName(name);
		role.setDescription(description);
		role.setDisabled(disabled);
		role.setPermissions(permissions);
		try {
			Role newRole = roleService.create(role);
			Assert.assertNotNull(newRole);
			Assert.assertEquals(id, newRole.getId());
			return newRole;
		} catch (ServiceException e) {
			log.error("createRole failed", e);
			throw e;
		}
	}
}
