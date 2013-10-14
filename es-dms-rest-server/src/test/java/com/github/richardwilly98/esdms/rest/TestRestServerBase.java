package com.github.richardwilly98.esdms.rest;

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
import java.util.Set;

import javax.ws.rs.NotSupportedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.UserService;
import com.github.richardwilly98.esdms.web.TestJerseyApplication;
import com.github.richardwilly98.esdms.web.TestRestGuiceServletConfig;
import com.google.inject.Inject;
import com.google.inject.servlet.GuiceFilter;

/*
 * TODO: Investigate why SSL does not work.
 * @see TestRestService for usage
 */
@Guice(modules = com.github.richardwilly98.esdms.inject.TestEsClientModule.class)
public class TestRestServerBase {

    protected final Logger log = Logger.getLogger(getClass());
    protected final static Credential adminCredential = new CredentialImpl.Builder().username(UserService.DEFAULT_ADMIN_LOGIN)
            .password(UserService.DEFAULT_ADMIN_PASSWORD.toCharArray()).build();
    private final Server server;
    final static ObjectMapper mapper = new ObjectMapper();
    protected static String adminToken;
    protected static Cookie adminCookie;
    private final Client restClient;
    // private final Client securedClient;
    public static final int HTTP_PORT = 8081;
    public static final int HTTPS_PORT = 50443;
    public static final String URL = "http://localhost:" + HTTP_PORT;
    public static final String SECURED_URL = "https://localhost:" + HTTPS_PORT;

    @Inject
    org.elasticsearch.client.Client client;

    protected TestRestServerBase() throws Exception {
        // mapper.configure(
        // DeserializationConfig.Feature.USE_ANNOTATIONS, false)
        // .configure(SerializationConfig.Feature.USE_ANNOTATIONS, false);
        server = new Server(HTTP_PORT);
        // Connector secureConnector = createSecureConnector();
        // server.setConnectors(new Connector[] {secureConnector});
        // ClientConfig config = new DefaultClientConfig();
        // config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
        // Boolean.TRUE);
        // config.getFeatures().add(JacksonJsonProvider.class);
        // restClient = Client.create(config);
        ClientConfig configuration = new ClientConfig();
        configuration.register(MultiPartFeature.class);
        // configuration.register(new JacksonFeature());
        restClient = ClientBuilder.newClient(configuration);
        // restClient.register(new JacksonFeature());
        // restClient = Client.create(new DefaultClientConfig(
        // JacksonJaxbJsonProvider.class));
        // securedClient = createSecuredClient();
    }

    final static Annotations[] BASIC_ANNOTATIONS = { Annotations.JACKSON };

    // private Client createSecuredClient() throws Exception {
    // TrustManager[ ] certs = new TrustManager[ ] {
    // new X509TrustManager() {
    // @Override
    // public X509Certificate[] getAcceptedIssuers() {
    // return null;
    // }
    // @Override
    // public void checkServerTrusted(X509Certificate[] chain, String authType)
    // throws CertificateException {
    // }
    // @Override
    // public void checkClientTrusted(X509Certificate[] chain, String authType)
    // throws CertificateException {
    // }
    // }
    // };
    //
    // ClientConfig config = new DefaultClientConfig();
    // SSLContext ctx = SSLContext.getInstance("SSL");
    // ctx.init(null, certs, new SecureRandom());
    // config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new
    // HTTPSProperties(new HostnameVerifier() {
    // @Override
    // public boolean verify(String hostname, SSLSession session) {
    // return true;
    // }
    // }, ctx));
    // return Client.create(config);
    // }

    @BeforeSuite
    public final void beforeSuite() throws Throwable {
        setUp();
    }

    @AfterSuite
    public final void afterSuite() throws Throwable {
        tearDown();
    }

    /*
     * Override this method to customize what will be run before all tests in
     * this suite have run.
     */
    protected void setUp() throws Throwable {
        log.info("*** setUp ***");
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setResourceBase("src/test/webapp/");
        webAppContext.setParentLoaderPriority(true);
        webAppContext.addEventListener(new TestRestGuiceServletConfig());
        webAppContext.addFilter(GuiceFilter.class, "/*", null);
        ServletHolder servlet = new ServletHolder();
        servlet.setName("jersey-servlet");
        servlet.setClassName("org.glassfish.jersey.servlet.ServletContainer");
        servlet.getInitParameters().put("javax.ws.rs.Application", TestJerseyApplication.class.getCanonicalName());
        webAppContext.addServlet(servlet, "/*");

        server.setHandler(webAppContext);
        server.start();

        loginAdminUser();
    }

    /*
     * Override this method to customize what will be run after all tests in
     * this suite have run.
     */
    protected void tearDown() throws Throwable {
        log.info("*** tearDown ***");
        logoutAdminUser();
        server.stop();
        tearDownElasticsearch();
    }

    private void tearDownElasticsearch() throws Exception {
        log.info("*** tearDownElasticsearch ***");
        client.admin().indices().prepareDelete().execute().actionGet();
        client.close();
    }

    // private Connector createSecureConnector() {
    // SslSocketConnector connector = new SslSocketConnector();
    // connector.setPort(HTTPS_PORT);
    // connector.setKeystore(".keystore");
    // connector.setKeyPassword("secret");
    // return connector;
    // }

    protected URI getBaseURI(boolean secured) {
        if (secured) {
            throw new NotSupportedException("https is not supported");
//            return UriBuilder.fromUri(SECURED_URL).build();
        } else {
            return UriBuilder.fromUri(URL).build();
        }
    }

    /**
     * Create a web resource whose URI refers to the base URI the Web
     * application is deployed at.
     * 
     * @return the created web resource
     */
    protected WebTarget target() {
        return restClient.target(getBaseURI(false));
    }

    /**
     * Get the client that is configured for this test.
     * 
     * @return the configured client.
     */
    protected Client client() {
        return restClient;
    }

    private void loginAdminUser() {
        try {
            log.debug("*** loginAdminUser ***");
            adminCookie = login(adminCredential);
            checkNotNull(adminCookie);
            adminToken = adminCookie.getValue();
        } catch (Throwable t) {
            log.error("loginAdminUser failed", t);
        }
    }

    protected Cookie login(Credential credential) throws Throwable {
        try {
            log.debug(String.format("login - %s", credential));
            WebTarget webResource = target().path("auth").path("login");
            log.debug(webResource);
            Response response = webResource.request(MediaType.APPLICATION_JSON).post(Entity.entity(credential, MediaType.APPLICATION_JSON));
            log.debug("status: " + response.getStatus());
            Assert.assertEquals(response.getStatus(), Status.OK.getStatusCode());
            for (NewCookie cookie : response.getCookies().values()) {
                if (RestAuthenticationService.ES_DMS_TICKET.equals(cookie.getName())) {
                    return new Cookie(cookie.getName(), cookie.getValue());
                }
            }
        } catch (Throwable t) {
            log.error("login failed", t);
            Assert.fail("login failed", t);
        }
        return null;
    }

    protected void logout(Cookie cookie) throws Throwable {
        log.debug(String.format("logout - %s", cookie));
        checkNotNull(cookie);
        WebTarget webResource = target().path("auth").path("logout");
        Response response = webResource.request().cookie(cookie).post(Entity.json(null));
        Assert.assertEquals(response.getStatus(), Status.OK.getStatusCode());
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new ServiceException(String.format("logout failed. Response status: %s", response.getStatus()));
        }

    }

    private void logoutAdminUser() throws Throwable {
        logout(adminCookie);
    }

    protected User createUser(String login, String password) throws Throwable {
        return createUser(login, password, null);
    }

    protected User createUser(String login, String password, Set<Role> roles) throws Throwable {
        log.trace(String.format("*** createUser - %s - %s ***", login, password));
        User user = new UserImpl.Builder().id(login).name(login).email(login).login(login).password(password.toCharArray()).roles(roles)
                .build();
        String json = mapper.writeValueAsString(user);
        log.trace(json);
        Response response = target().path(RestUserService.USERS_PATH).request(MediaType.APPLICATION_JSON).cookie(adminCookie)
                .post(Entity.json(user));
        log.debug(String.format("status: %s", response.getStatus()));
        Assert.assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            throw new ServiceException(String.format("createUser %s failed. Response status: %s", login, response.getStatus()));
        }
        URI uri = response.getLocation();
        Assert.assertNotNull(uri);
        log.trace(String.format("getItem - %s", uri));
        response = client().target(uri).request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(User.class);
        }
        log.warn(String.format("status: %s", response.getStatus()));
        return null;
    }

}
