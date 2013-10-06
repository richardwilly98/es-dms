package com.github.richardwilly98.esdms.client;

import org.apache.log4j.Logger;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.exception.ServiceException;

public abstract class RestClientBaseTest {

	protected final Logger log = Logger
			.getLogger(getClass());

	private String url = "http://localhost:9000";
	static final String DEFAULT_ADMIN_LOGIN = "admin";
	static final char[] DEFAULT_ADMIN_PASSWORD = "secret".toCharArray();
	private RestAuthenticationServiceClient restAuthenticationServiceClient;
	private RestUserServiceClient restUserServiceClient;

	protected RestAuthenticationServiceClient getRestAuthenticationServiceClient() {
		if (restAuthenticationServiceClient == null) {
			restAuthenticationServiceClient = new RestAuthenticationServiceClient(
					url);
		}
		return restAuthenticationServiceClient;
	}

	protected RestUserServiceClient getRestUserServiceClient() {
		if (restUserServiceClient == null) {
			restUserServiceClient = new RestUserServiceClient(
					url);
		}
		return restUserServiceClient;
	}

	protected String loginAsAdmin() throws ServiceException {
		return getRestAuthenticationServiceClient().login(
				new CredentialImpl.Builder().username(DEFAULT_ADMIN_LOGIN)
						.password(DEFAULT_ADMIN_PASSWORD).build());
	}

}
