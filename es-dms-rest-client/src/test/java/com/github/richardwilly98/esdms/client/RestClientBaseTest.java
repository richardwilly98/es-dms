package com.github.richardwilly98.esdms.client;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.TestRestServerBase;
import com.github.richardwilly98.esdms.services.UserService;

@Guice(modules = com.github.richardwilly98.esdms.inject.TestEsClientModule.class)
public abstract class RestClientBaseTest extends TestRestServerBase {

	private RestAuthenticationService restAuthenticationServiceClient;
	private RestUserService restUserServiceClient;
	private RestRoleService restRoleServiceClient;
	private final String url;

	RestClientBaseTest() throws Exception {
		super();
		url = getBaseURI(false).toString();
		log.debug("getBaseURI: " + url);
	}

	protected final Logger log = Logger
			.getLogger(getClass());

    @BeforeClass
    public void beforeClass() throws Throwable {
    	setUp();
    }
    
    @AfterClass
    public void afterClass() throws Throwable {
    	tearDown();
    }
    
	protected RestAuthenticationService getRestAuthenticationServiceClient() {
		if (restAuthenticationServiceClient == null) {
			restAuthenticationServiceClient = new RestAuthenticationService(
					url);
		}
		return restAuthenticationServiceClient;
	}

	protected RestUserService getRestUserServiceClient() {
		if (restUserServiceClient == null) {
			restUserServiceClient = new RestUserService(
					url);
		}
		return restUserServiceClient;
	}

	protected RestRoleService getRestRoleServiceClient() {
		if (restRoleServiceClient == null) {
			restRoleServiceClient = new RestRoleService(url);
		}
		return restRoleServiceClient;
	}

	protected String loginAsAdmin() throws ServiceException {
		return getRestAuthenticationServiceClient().login(
				new CredentialImpl.Builder().username(UserService
						.DEFAULT_ADMIN_LOGIN)
						.password(UserService.DEFAULT_ADMIN_PASSWORD.toCharArray()).build());
	}

}
