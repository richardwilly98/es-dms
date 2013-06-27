package test.github.richardwilly98.esdms.rest;

/*
 * #%L
 * es-dms-site
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;

import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.testng.Assert;

import test.github.richardwilly98.esdms.web.TestRestGuiceServletConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.rest.RestAuthencationService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

//@Guice(modules = TestEsClientModule.class)
public class GuiceAndJerseyTestBase<T extends ItemBase> extends JerseyTest {

	final protected Logger log = Logger.getLogger(this.getClass());
	protected final static Credential adminCredential = new CredentialImpl.Builder().username(UserService.DEFAULT_ADMIN_LOGIN).password(UserService.DEFAULT_ADMIN_PASSWORD).build();
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

//	@Inject
	org.elasticsearch.client.Client client;

	// @Override
	// protected TestContainerFactory getTestContainerFactory()
	// throws TestContainerException {
	// return new OnePerAppDescriptorTestContainerFactory(
	// super.getTestContainerFactory());
	// }
	//
	
//	@BeforeSuite
	public void initTestContainer() throws Exception {
		log.debug("*** initTestContainer ***");
		super.setUp();
		client().setFollowRedirects(false);
		client().addFilter(new LoggingFilter());
		loginAdminUser();
	}

	private void loginAdminUser() {
		try {
			log.debug("*** loginAdminUser ***");
			adminCookie = login(adminCredential);
			Assert.assertNotNull(adminCookie);
			adminToken = adminCookie.getValue();
			Assert.assertNotNull(adminToken);
		} catch (Throwable t) {
			log.error("loginAdminUser failed", t);
			Assert.fail("loginAdminUser failed", t);
		}
	}

	protected Cookie login(Credential credential) {
		try {
			log.debug("*** login ***");
			WebResource webResource = resource().path("auth").path("login");
			log.debug(webResource);
			ClientResponse response = webResource.type(
					MediaType.APPLICATION_JSON).post(ClientResponse.class,
					credential);
			log.debug("status: " + response.getStatus());
			Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
			for (NewCookie cookie : response.getCookies()) {
				if (RestAuthencationService.ES_DMS_TICKET.equals(cookie
						.getName())) {
					return new Cookie(cookie.getName(), cookie.getValue());
				}
			}
		} catch (Throwable t) {
			log.error("login failed", t);
			Assert.fail("login failed", t);
		}
		return null;
	}

	protected void logout(Cookie cookie) {
		log.debug("*** logout ***");
		checkNotNull(cookie);
		WebResource webResource = resource().path("auth").path("logout");
		ClientResponse response = webResource.cookie(cookie).post(
				ClientResponse.class);
		log.debug("status: " + response.getStatus());
		Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
	}

	private void logoutAdminUser() {
		try {
			log.debug("*** logoutAdminUser ***");
			logout(adminCookie);
		} catch (Throwable t) {
			Assert.fail("logoutAdminUser failed", t);
		}
	}

//	@AfterSuite
	public void tearDownTestContainer() throws Exception {
		log.debug("*** tearDownTestContainer ***");
		logoutAdminUser();
		super.tearDown();
		tearDownElasticsearch();
	}

	private void tearDownElasticsearch() throws Exception {
		log.info("*** tearDownElasticsearch ***");
		client.admin().indices().prepareDelete().execute().actionGet();
		client.close();
	}

	protected T getItem(String id, Class<T> type, String path) throws Throwable {
		ClientResponse response = resource().path(path).path(id)
				.cookie(adminCookie).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		log.debug(String.format("status: %s", response.getStatus()));
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return response.getEntity(type);
		}
		return null;
	}

	protected T getItem(URI uri, Class<T> type) throws Throwable {
		log.debug(String.format("getItem - %s", uri));
		ClientResponse response = client().resource(uri).cookie(adminCookie)
				.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		log.debug(String.format("status: %s", response.getStatus()));
//		log.debug(String.format("get - body: %s", response.getEntity(String.class)));
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return response.getEntity(type);
		}
		return null;
	}

	protected T updateItem(ItemBase item, Class<T> type, String path)
			throws Throwable {
		ClientResponse response = resource().path(path).path(item.getId())
				.cookie(adminCookie).type(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, item);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
		return response.getEntity(type);
	}

	protected void deleteItem(String id, String path) throws Throwable {
		ClientResponse response = resource().path(path).path(id)
				.cookie(adminCookie).type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
	}

}
