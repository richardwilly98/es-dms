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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
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

import test.github.richardwilly98.esdms.inject.TestEsClientModule;
import test.github.richardwilly98.esdms.web.TestRestGuiceServletConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.rest.RestAuthencationService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.inject.Inject;
import com.google.inject.servlet.GuiceFilter;

/*
 * TODO: Investigate why SSL does not work.
 */
@Guice(modules = TestEsClientModule.class)
public class GuiceAndJettyTestBase<T extends ItemBase> {

    protected final Logger log = Logger.getLogger(getClass());
    protected final static Credential adminCredential = new CredentialImpl.Builder().username(UserService.DEFAULT_ADMIN_LOGIN)
	    .password(UserService.DEFAULT_ADMIN_PASSWORD).build();
    private final Server server;
    final static ObjectMapper mapper = new ObjectMapper();
    protected static String adminToken;
    protected static Cookie adminCookie;
    private final Client restClient;
    // private final Client securedClient;
    private static final int HTTP_PORT = 8081;
    private static final int HTTPS_PORT = 50443;

    @Inject
    org.elasticsearch.client.Client client;

    GuiceAndJettyTestBase() throws Exception {
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

    protected T get(String id, Class<T> type, String path) throws Throwable {
	Response response = target().path(path).path(id).request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
	log.debug(String.format("status: %s", response.getStatus()));
	if (response.getStatus() == Status.OK.getStatusCode()) {
	    return response.readEntity(type);
	}
	return null;
    }

    protected T get(URI uri, Class<T> type) throws Throwable {
	log.debug(String.format("getItem - %s", uri));
	Response response = client().target(uri).request().cookie(adminCookie).accept(MediaType.APPLICATION_JSON).get();
	log.debug(String.format("status: %s", response.getStatus()));
	// log.debug(String.format("get - body: %s",
	// response.getEntity(String.class)));
	if (response.getStatus() == Status.OK.getStatusCode()) {
	    // return deserialize(response.getEntity(String.class), type);
	    return response.readEntity(type);
	}
	return null;
    }

    private T deserialize(String json, Class<T> type) {
	try {
	    log.debug(String.format("deserialize in %s -> %s", type.getName(), json));
	    return mapper.readValue(json, type);
	} catch (Throwable t) {
	    log.error("deserialize failed", t);
	}
	return null;
    }

    protected T update(ItemBase item, Class<T> type, String path) throws Throwable {
	Response response = target().path(path).path(item.getId()).request(MediaType.APPLICATION_JSON).cookie(adminCookie)
	        .put(Entity.json(item));
	log.debug(String.format("status: %s", response.getStatus()));
	Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
	return response.readEntity(type);
    }

    protected void delete(String id, String path) throws Throwable {
	Response response = target().path(path).path(id).request().cookie(adminCookie).delete();
	log.debug(String.format("status: %s", response.getStatus()));
	Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
    }

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
    public void initTestContainer() throws Exception {
	log.info("*** initTestContainer ***");
	WebAppContext webAppContext = new WebAppContext();
	webAppContext.setContextPath("/");
	webAppContext.setResourceBase("src/test/webapp/");
	webAppContext.setParentLoaderPriority(true);
	webAppContext.addEventListener(new TestRestGuiceServletConfig());
	webAppContext.addFilter(GuiceFilter.class, "/*", null);
	ServletHolder servlet = new ServletHolder();
	servlet.setName("jersey-servlet");
	servlet.setClassName("org.glassfish.jersey.servlet.ServletContainer");
	servlet.getInitParameters().put("javax.ws.rs.Application", "test.github.richardwilly98.esdms.web.TestJerseyApplication");
	webAppContext.addServlet(servlet, "/*");

	server.setHandler(webAppContext);
	server.start();

	// ClientConfig config = new DefaultClientConfig();
	// config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
	// Boolean.TRUE);
	// config.getClasses().add(JacksonJaxbJsonProvider.class);
	// // config.getFeatures().add(JacksonJsonProvider.class);
	// restClient = Client.create(config);

	loginAdminUser();
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
	    return UriBuilder.fromUri("https://localhost/").port(HTTPS_PORT).build();
	} else {
	    return UriBuilder.fromUri("http://localhost/").port(HTTP_PORT).build();
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

    // public WebResource securedResource() {
    // return securedClient.resource(getBaseURI(true));
    // }

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
	    WebTarget webResource = target().path("auth").path("login");
	    log.debug(webResource);
	    Response response = webResource.request(MediaType.APPLICATION_JSON).post(Entity.entity(credential, MediaType.APPLICATION_JSON));
	    log.debug("status: " + response.getStatus());
	    Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
	    for (NewCookie cookie : response.getCookies().values()) {
		if (RestAuthencationService.ES_DMS_TICKET.equals(cookie.getName())) {
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
	WebTarget webResource = target().path("auth").path("logout");
	Response response = webResource.request().cookie(cookie).post(Entity.json(null));
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

    @AfterSuite
    public void tearDownTestContainer() throws Exception {
	log.info("*** tearDownTestContainer ***");
	logoutAdminUser();
	server.stop();
	tearDownElasticsearch();
    }

    private void tearDownElasticsearch() throws Exception {
	log.info("*** tearDownElasticsearch ***");
	client.admin().indices().prepareDelete().execute().actionGet();
	client.close();
    }
}
