package test.github.richardwilly98.rest;

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
import com.github.richardwilly98.rest.RestAuthencationService;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

public abstract class GuiceAndJerseyTestBase extends JerseyTest {

	Logger log = Logger.getLogger(this.getClass());
	final static Credential adminCredential = new Credential("admin", "secret");
	final static ObjectMapper mapper = new ObjectMapper();
	static String adminToken;

	// Fire up jersey with Guice
	private static final AppDescriptor APP_DESCRIPTOR = new WebAppDescriptor.Builder(
			"com.github.richardwilly98.rest")
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
		setUp();
		createAdminUser();
	}

	private void createAdminUser() {
		try {
			WebResource webResource = resource().path("auth").path("login");
			ObjectMapper mapper = new ObjectMapper();
			ClientResponse response = webResource
			// .type(MediaType.APPLICATION_JSON).entity(credential)
			// .post(ClientResponse.class);
					.type(MediaType.APPLICATION_JSON).post(
							ClientResponse.class,
							mapper.writeValueAsString(adminCredential));
			log.debug("body: " + response.getEntity(String.class));
			log.debug("status: " + response.getStatus());
			Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
			for (NewCookie cookie : response.getCookies()) {
				if (RestAuthencationService.ES_DMS_TICKET.equals(cookie
						.getName())) {
					adminToken = cookie.getValue();
				}
			}
			Assert.assertNotNull(adminToken);
		} catch (Throwable t) {
			Assert.fail("createAdminUser failed", t);
		}

	}

	@AfterSuite
	public void tearDownTestContainer() throws Exception {
		tearDown();
	}
}
