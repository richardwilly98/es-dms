package test.github.richardwilly98.rest;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;

import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import test.github.richardwilly98.web.TestRestGuiceServletConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.services.UserService;
import com.github.richardwilly98.rest.RestAuthencationService;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

public abstract class GuiceAndJerseyTestBase extends JerseyTest {

	final protected Logger log = Logger.getLogger(this.getClass());
	final static Credential adminCredential = new Credential(
			UserService.DEFAULT_ADMIN_LOGIN, UserService.DEFAULT_ADMIN_PASSWORD);
	final static ObjectMapper mapper = new ObjectMapper();
	protected String adminToken;
	protected Cookie adminCookie;

	// Fire up jersey with Guice
	private static final AppDescriptor APP_DESCRIPTOR = new WebAppDescriptor.Builder()
			.contextListenerClass(TestRestGuiceServletConfig.class)
			.filterClass(GuiceFilter.class)
			.contextPath("es-dms-site")
			.servletPath("/")
			.clientConfig(
					new DefaultClientConfig(JacksonJaxbJsonProvider.class))
			.build();

	public GuiceAndJerseyTestBase() throws Exception {
		super(APP_DESCRIPTOR);
	}

	// @Override
	// protected TestContainerFactory getTestContainerFactory()
	// throws TestContainerException {
	// return new OnePerAppDescriptorTestContainerFactory(
	// super.getTestContainerFactory());
	// }
	//
	@BeforeSuite
	public void initTestContainer() throws Exception {
		super.setUp();
		client().setFollowRedirects(false);
		client().addFilter(new LoggingFilter());
		loginAdminUser();
	}

	private void loginAdminUser() {
		try {
			log.debug("*** loginAdminUser ***");
			WebResource webResource = resource().path("auth").path("login");
			ObjectMapper mapper = new ObjectMapper();
			ClientResponse response = webResource.type(
					MediaType.APPLICATION_JSON).post(ClientResponse.class,
					mapper.writeValueAsString(adminCredential));
			log.debug("status: " + response.getStatus());
			Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
			for (NewCookie cookie : response.getCookies()) {
				if (RestAuthencationService.ES_DMS_TICKET.equals(cookie
						.getName())) {
					adminToken = cookie.getValue();
					adminCookie = new Cookie(cookie.getName(),
							cookie.getValue());
				}
			}
			Assert.assertNotNull(adminToken);
			Assert.assertNotNull(adminCookie);
		} catch (Throwable t) {
			Assert.fail("loginAdminUser failed", t);
		}
	}

	private void logoutAdminUser() {
		try {
			log.debug("*** logoutAdminUser ***");
			WebResource webResource = resource().path("auth").path("logout");
			ClientResponse response = webResource.cookie(adminCookie).post(
					ClientResponse.class);
			log.debug("status: " + response.getStatus());
			Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
		} catch (Throwable t) {
			Assert.fail("logoutAdminUser failed", t);
		}
	}

	@AfterSuite
	public void tearDownTestContainer() throws Exception {
		logoutAdminUser();
		super.tearDown();
	}
}
