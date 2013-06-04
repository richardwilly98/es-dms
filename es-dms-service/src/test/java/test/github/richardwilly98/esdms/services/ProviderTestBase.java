package test.github.richardwilly98.esdms.services;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Guice;

import test.github.richardwilly98.esdms.inject.ProviderModule;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.PermissionImpl;
import com.github.richardwilly98.esdms.RoleImpl;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.DocumentService;
import com.github.richardwilly98.esdms.services.PermissionService;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.inject.Inject;

/*
 * Base class for all test providers
 */
@Guice(modules = ProviderModule.class)
public class ProviderTestBase {

	final protected Logger log = Logger.getLogger(this.getClass());
	final static Credential adminCredential = new CredentialImpl(
			UserService.DEFAULT_ADMIN_LOGIN, UserService.DEFAULT_ADMIN_PASSWORD);
	final static Map<String, User> users = newHashMap();
	final static Set<Permission> permissions = newHashSet();
	final static Set<Role> roles = newHashSet();
	static String adminToken;

	static {
		// Permission permissionCreateDocument = new
		// PermissionImpl("document:create");
		Permission permissionCreateDocument = new PermissionImpl.Builder()
				.name("document:create").build();
		permissionCreateDocument.setDescription("Create document");
		permissions.add(permissionCreateDocument);

		// Permission permissionDeleteDocument = new
		// PermissionImpl("document:delete");
		Permission permissionDeleteDocument = new PermissionImpl.Builder()
				.name("document:delete").build();
		permissionDeleteDocument.setDescription("Delete document");
		permissions.add(permissionDeleteDocument);

		// Permission permissionReadDocument = new
		// PermissionImpl("document:read");
		Permission permissionReadDocument = new PermissionImpl.Builder().name(
				"document:read").build();
		permissionReadDocument.setDescription("Read document");
		permissions.add(permissionReadDocument);

		// Role collaboratorRole = new RoleImpl();
		// collaboratorRole.setId("collaborator");
		// collaboratorRole.setName(collaboratorRole.getId());
		// collaboratorRole.setDescription("Collaborator");
		// collaboratorRole.setDisabled(false);
		// collaboratorRole.setPermissions(permissions);
		Role collaboratorRole = new RoleImpl.Builder().permissions(permissions)
				.id("collaborator").name("Collaborator")
				.description("Collaborator").disabled(false).build();
		roles.add(collaboratorRole);

		// Role readerRole = new RoleImpl();
		// readerRole.setId("reader");
		// readerRole.setName(readerRole.getId());
		// readerRole.setDescription("Reader");
		// readerRole.setDisabled(false);
		Set<Permission> ps = new HashSet<Permission>();
		ps.add(permissionReadDocument);
		Role readerRole = new RoleImpl.Builder().permissions(ps).id("reader")
				.name("Reader").description("Reader").disabled(false).build();
		// readerRole.setPermissions(ps);
		roles.add(readerRole);

		// User user = new UserImpl();
		// user.setId("richard.louapre@gmail.com");
		// user.setEmail(user.getId());
		// user.setName("Richard");
		// user.setDisabled(false);
		// user.setCity("Jersey City");
		// user.setPassword("secret");
		User user = new UserImpl.Builder().id("richard.louapre@gmail.com")
				.name("Richard").disabled(false).city("Jersey City")
				.password("secret").email("richard.louapre@gmail.com").build();
		user.addRole(readerRole);
		users.put(user.getLogin(), user);

		// user = new UserImpl();
		// user.setId("danilo.sandron@gmail.com");
		// user.setEmail(user.getId());
		// user.setName("Danilo");
		// user.setDisabled(false);
		// user.setCity("Bankok");
		// user.setPassword("segreto");
		user = new UserImpl.Builder().id("danilo.sandron@gmail.com")
				.name("Danilo").disabled(false).city("Bankok")
				.password("segreto").email("danilo.sandron@gmail.com").build();
		user.addRole(collaboratorRole);
		users.put(user.getLogin(), user);
	}

	@Inject
	Client client;

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

	@BeforeSuite
	public void beforeSuite() {
		log.info("** beforeSuite **");
		loginAdminUser();
		createPermissions();
		createRoles();
		createUsers();
	}

	@AfterSuite
	public void tearDownSuite() throws Exception {
		log.info("*** tearDownSuite ***");
		tearDownElasticsearch();
	}

	private void tearDownElasticsearch() throws Exception {
		log.info("*** tearDownElasticsearch ***");
		client.admin().indices().prepareDelete().execute().actionGet();
		client.close();
	}

	protected void loginAdminUser() {
		try {
			adminToken = authenticationService.login(adminCredential);
		} catch (ServiceException ex) {
			log.error("loginAdminUser failed", ex);
			Assert.fail("loginAdminUser failed", ex);
		}
	}

	private void createUsers() {
		try {
			for (User user : users.values()) {
				String password = user.getPassword();
				createUser(user);
				user.setPassword(password);
			}
		} catch (ServiceException ex) {
			log.error("createUsers failed", ex);
			Assert.fail("createUsers failed", ex);
		}
	}

	private void createRoles() {
		try {
			for (Role role : roles) {
				createRole(role);
			}
		} catch (ServiceException ex) {
			log.error("createRoles failed", ex);
			Assert.fail("createRoles failed", ex);
		}
	}

	private void createPermissions() {
		try {
			for (Permission permission : permissions) {
				createPermission(permission);
			}
		} catch (ServiceException ex) {
			log.error("createPermissions failed", ex);
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

	protected Permission createPermission(Permission permission)
			throws ServiceException {
		try {
			Permission newPermission = permissionService.create(permission);
			Assert.assertNotNull(newPermission);
			Assert.assertEquals(permission.getId(), newPermission.getId());
			return newPermission;
		} catch (ServiceException e) {
			log.error("createPermission failed", e);
			throw e;
		}
	}

	protected Permission createPermission(String name, String description,
			boolean disabled) throws ServiceException {
		Assert.assertTrue(!(name == null || name.isEmpty()));

		// PermissionImpl permission = new PermissionImpl();
		// String id = String.valueOf(name);
		// permission.setId(id);
		// permission.setName(name);
		// permission.setDescription(description);
		// permission.setDisabled(disabled);
		Permission permission = new PermissionImpl.Builder().id(name)
				.name(name).description(description).disabled(disabled).build();
		return createPermission(permission);
	}

	protected User createUser(User user) throws ServiceException {
		try {
			User newUser = userService.create(user);
			Assert.assertNotNull(newUser);
			Assert.assertEquals(user.getId(), newUser.getId());
			return newUser;
		} catch (ServiceException e) {
			log.error("createUser failed", e);
			throw e;
		}
	}

	protected User createUser(String name, String description,
			boolean disabled, String email, String password, Set<Role> roles)
			throws ServiceException {
		// User user = new UserImpl();
		// String id = String.valueOf(email);
		// user.setId(id);
		// user.setName(name);
		// user.setDescription(description);
		// user.setDisabled(disabled);
		// user.setEmail(email);
		// user.setPassword(password);
		User user = new UserImpl.Builder().id(email).name(name)
				.description(description).disabled(disabled).email(email)
				.password(password).roles(roles).build();
		return createUser(user);
	}

	protected Role createRole(Role role) throws ServiceException {
		try {
			Role newRole = roleService.create(role);
			Assert.assertNotNull(newRole);
			Assert.assertEquals(role.getId(), newRole.getId());
			return newRole;
		} catch (ServiceException e) {
			log.error("createRole failed", e);
			throw e;
		}
	}

	protected Role createRole(String name, String description,
			boolean disabled, Set<Permission> permissions)
			throws ServiceException {
		log.trace("Preparing to create permission: " + name);
		Assert.assertTrue(!(name == null || name.isEmpty()));
		// Role role = new RoleImpl();
		// String id = String.valueOf(name);
		// role.setId(id);
		// role.setName(name);
		// role.setDescription(description);
		// role.setDisabled(disabled);
		// role.setPermissions(permissions);
		Role role = new RoleImpl.Builder().id(name).name(name)
				.description(description).disabled(disabled)
				.permissions(permissions).build();
		return createRole(role);
	}
}
