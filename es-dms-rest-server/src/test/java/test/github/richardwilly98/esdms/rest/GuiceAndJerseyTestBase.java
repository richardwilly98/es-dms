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

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.testng.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.services.UserService;
import com.github.richardwilly98.esdms.web.TestJerseyApplication;
import com.google.common.collect.ImmutableMap;

//@Guice(modules = TestEsClientModule.class)
public class GuiceAndJerseyTestBase<T extends ItemBase> extends JerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        final TestJerseyApplication application = new TestJerseyApplication(null);
        application.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        return application;
    }

    final protected Logger log = Logger.getLogger(this.getClass());
    protected final static Credential adminCredential = new CredentialImpl.Builder().username(UserService.DEFAULT_ADMIN_LOGIN)
            .password(UserService.DEFAULT_ADMIN_PASSWORD.toCharArray()).build();
    final static ObjectMapper mapper = new ObjectMapper();
    protected String adminToken;
    protected MultivaluedMap<String,Object> adminAuthenticationHeader;

    // Fire up jersey with Guice
    // private static final AppDescriptor APP_DESCRIPTOR = new
    // WebAppDescriptor.Builder()
    // .contextListenerClass(TestRestGuiceServletConfig.class)
    // .filterClass(GuiceFilter.class)
    // .contextPath("es-dms-site")
    // .servletPath("/")
    // .clientConfig(
    // new DefaultClientConfig(JacksonJaxbJsonProvider.class))
    // .build();

    public GuiceAndJerseyTestBase() throws Exception {
        // super(APP_DESCRIPTOR);
    }

    // @Inject
    org.elasticsearch.client.Client client;

    // @Override
    // protected TestContainerFactory getTestContainerFactory()
    // throws TestContainerException {
    // return new OnePerAppDescriptorTestContainerFactory(
    // super.getTestContainerFactory());
    // }
    //

    @Override
    protected void configureClient(ClientConfig clientConfig) {
        clientConfig.property(ClientProperties.FOLLOW_REDIRECTS, false);
        clientConfig.register(LoggingFilter.class);
    }

    // @BeforeSuite
    public void initTestContainer() throws Exception {
        log.debug("*** initTestContainer ***");
        super.setUp();
        // client().setFollowRedirects(false);
        // client().addFilter(new LoggingFilter());
        loginAdminUser();
    }

    private void loginAdminUser() {
        try {
            log.debug("*** loginAdminUser ***");
            adminAuthenticationHeader = login(adminCredential);
            Assert.assertNotNull(adminAuthenticationHeader);
            adminToken = adminAuthenticationHeader.getFirst(User.ES_DMS_TICKET).toString();
            Assert.assertNotNull(adminToken);
        } catch (Throwable t) {
            log.error("loginAdminUser failed", t);
            Assert.fail("loginAdminUser failed", t);
        }
    }

    protected MultivaluedMap<String, Object> login(Credential credential) throws Throwable {
        try {
            log.debug("*** login ***");
            WebTarget webResource = target().path("auth").path("login");
            Response response = webResource.request(MediaType.APPLICATION_JSON).post(
                    Entity.entity(credential, MediaType.APPLICATION_JSON_TYPE));
            log.debug("status: " + response.getStatus());
            Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
            for (NewCookie cookie : response.getCookies().values()) {
                if (User.ES_DMS_TICKET.equals(cookie.getName())) {
                    new MultivaluedHashMap<String, Object>(ImmutableMap.of(User.ES_DMS_TICKET, cookie.getValue()));
                }
            }
        } catch (Throwable t) {
            log.error("login failed", t);
            Assert.fail("login failed", t);
        }
        return null;
    }

    protected void logout(MultivaluedMap<String, Object> header) {
        log.debug("*** logout ***");
        checkNotNull(header);
        WebTarget webResource = target().path("auth").path("logout");
        Response response = webResource.request().headers(header).post(null);
        log.debug("status: " + response.getStatus());
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
    }

    private void logoutAdminUser() {
        try {
            log.debug("*** logoutAdminUser ***");
            logout(adminAuthenticationHeader);
        } catch (Throwable t) {
            Assert.fail("logoutAdminUser failed", t);
        }
    }

    // @AfterSuite
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
        Response response = target().path(path).path(id).request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader).get();
        log.debug(String.format("status: %s", response.getStatus()));
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(type);
        }
        return null;
    }

    protected T getItem(URI uri, Class<T> type) throws Throwable {
        log.debug(String.format("getItem - %s", uri));
        Response response = client().target(uri).request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader).get();
        log.debug(String.format("status: %s", response.getStatus()));
        // log.debug(String.format("get - body: %s",
        // response.getEntity(String.class)));
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(type);
        }
        return null;
    }

    protected T updateItem(ItemBase item, Class<T> type, String path) throws Throwable {
        Response response = target().path(path).path(item.getId()).request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader)
                .put(Entity.entity(item, MediaType.APPLICATION_JSON_TYPE));
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
        return response.readEntity(type);
    }

    protected void deleteItem(String id, String path) throws Throwable {
        Response response = target().path(path).path(id).request(MediaType.APPLICATION_JSON).headers(adminAuthenticationHeader).delete();
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
    }

}
