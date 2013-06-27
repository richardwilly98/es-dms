package test.github.richardwilly98.esdms.api;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.PermissionImpl;
import com.github.richardwilly98.esdms.RoleImpl;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;
import com.google.common.collect.ImmutableSet;

public class UserSerializationTest {

	private static Logger log = Logger.getLogger(UserSerializationTest.class);
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@BeforeClass
	public void initialize() {
		log.info("*** initialize ***");
	}

	@Test
	public void testSerializeDeserializeUser() throws Throwable {
		log.debug("*** testSerializeDeserializeUser ***");
		String id = "user-" + System.currentTimeMillis();
		String name = id;
		String email = id + "@gmail.com";
		String password = "secret";
		User user = new UserImpl.Builder().password(password).id(id).name(name).email(email).build();
		log.debug(user);
		String json = mapper.writeValueAsString(user);
		log.debug(json);
		Assert.assertNotNull(json);
		User user2 = mapper.readValue(json, User.class);
		log.debug(user2);
		Assert.assertEquals(user, user2);
	}
	
	@Test
	public void testUserHasRole() throws Throwable {
		log.debug("*** testUserHasRole ***");
		String id = "user-" + System.currentTimeMillis();
		String name = id;
		String email = id + "@gmail.com";
		String password = "secret";
		Role role = new RoleImpl.Builder().id("my-role").name("My role").build();
		Set<Role> roles = newHashSet(ImmutableSet.of(role));
		User user = new UserImpl.Builder().password(password).id(id).name(name).email(email).roles(roles).build();
		log.debug("user: " + user);
		Assert.assertTrue(user.hasRole(role));
		String json = mapper.writeValueAsString(user);
		log.debug(json);
		Assert.assertNotNull(json);
		User user2 = mapper.readValue(json, User.class);
		log.debug("user2: " + user2);
		Assert.assertEquals(user, user2);
		Assert.assertTrue(user2.hasRole(role));
	}

	@Test
	public void testSerializeDeserializePermission() throws Throwable {
		log.debug("*** testSerializeDeserializePermission ***");
		String id = "permission-" + System.currentTimeMillis();
		String name = id;
		Permission permission = new PermissionImpl.Builder().id(id).name(name).access("access1").build();
		log.debug(permission);
		String json = mapper.writeValueAsString(permission);
		log.debug(json);
		Assert.assertNotNull(json);
		Permission permission2 = mapper.readValue(json, Permission.class);
		log.debug(permission2);
		Assert.assertEquals(permission, permission2);
		Permission permission3 = new PermissionImpl.Builder().id(id).name(name).access("access1").build();
		Assert.assertNotSame(permission3, permission2);
	}
}
