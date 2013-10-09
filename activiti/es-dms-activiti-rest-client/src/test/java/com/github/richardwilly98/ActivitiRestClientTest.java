package com.github.richardwilly98;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class ActivitiRestClientTest {

    protected final Logger log = Logger.getLogger(getClass());
    private final Client restClient;
    private static final int HTTP_PORT = 8080;
    private static final int HTTPS_PORT = 8443;
    protected static final String ADMIN_USERNAME = "kermit";
    private static final String ADMIN_PASSWORD = "secret";

    public ActivitiRestClientTest() {
        ClientConfig configuration = new ClientConfig();
        configuration.register(MultiPartFeature.class);
        // configuration.register(new JacksonFeature());
        restClient = ClientBuilder.newClient(configuration);
    }

    protected URI getBaseURI(boolean secured) {
        int port = HTTP_PORT;
        String uriRoot = "http://localhost/";
        if (secured) {
            port = HTTPS_PORT;
            uriRoot = "https://localhost/";
        }
        return UriBuilder.fromUri(uriRoot).port(port).path("activiti-rest/service").build();
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

    @BeforeClass
    public void beforeClass() {
        restClient.register(new HttpBasicAuthFilter(ADMIN_USERNAME, ADMIN_PASSWORD));
    }

    @AfterClass
    public void afterClass() {
        restClient.close();
    }

}
