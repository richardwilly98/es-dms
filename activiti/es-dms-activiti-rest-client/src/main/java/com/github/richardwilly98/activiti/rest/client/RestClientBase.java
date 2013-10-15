package com.github.richardwilly98.activiti.rest.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

public abstract class RestClientBase {

    protected final Logger log = Logger.getLogger(getClass());
    private final Client restClient;

    // Url should include activiti-rest/service
    private final URI url;
    private String token;

    public RestClientBase(final URI url) {
        this.url = url;
        ClientConfig configuration = new ClientConfig();
        configuration.register(MultiPartFeature.class);
        restClient = ClientBuilder.newClient(configuration);
    }

    protected URI getBaseURI() {
        return UriBuilder.fromUri(url).build();
    }

    protected WebTarget target() {
        return target(null);
    }

    /**
     * Create a web resource whose URI refers to the base URI the Web
     * application is deployed at.
     * 
     * @return the created web resource
     */
    protected WebTarget target(String path) {
        WebTarget target = restClient.target(getBaseURI());
//        if (!Strings.isNullOrEmpty(path)) {
//            target.path(path);
//        }
//        if (!Strings.isNullOrEmpty(token)) {
//            target.request().cookie(new Cookie("ES_DMS_TICKET", token));
//        }
        return target;
    }

    public void setCredentials(String username, byte[] password) {
        restClient.register(new HttpBasicAuthFilter(username, password));
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
