package test.github.richardwilly98.services;

import static org.elasticsearch.common.io.Streams.copyToBytesFromClasspath;

import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.common.Base64;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.services.UserService;
import com.github.richardwilly98.services.UserProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test
public class UserProviderTest {
	
	private static Logger log = Logger.getLogger(DocumentProviderTest.class);

	@BeforeSuite
	public void beforeSuite() throws Exception {
	}

	@BeforeClass
	public void setupServer() {
	}

	@AfterClass
	public void closeServer() {
	}
	
//	protected UserService getUserProvider() {
//		Injector injector = Guice.createInjector(new ProviderModule());
//		return injector.getInstance(UserProvider.class);
//	}
	
	private void testCreateUser(String name, String description, boolean disabled) throws Throwable {
		//UserService provider = getUserProvider();
		User user = new User();
		
		user.setId(String.valueOf(System.currentTimeMillis()));
		user.setName(name);
		user.setDescription(description);
		user.setDisabled(disabled);
		// TODO:

	}

	@Test
	public void testCreateUser() throws Throwable {
		log.info("Start testCreateUser");
		testCreateUser("Richard", "Lead developer", false);
		testCreateUser("Danilo", "Mezza calzetta", true);
	}

}
