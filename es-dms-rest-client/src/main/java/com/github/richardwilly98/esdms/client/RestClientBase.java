package com.github.richardwilly98.esdms.client;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.exception.ServiceException;

public abstract class RestClientBase {

    public static final String ES_DMS_TICKET = "ES_DMS_TICKET";

    protected final Logger log = Logger.getLogger(getClass());
    private final String url;
    protected final Client restClient;

    protected RestClientBase(final String url) {
        checkNotNull(url);
        this.url = url;
        ClientConfig configuration = new ClientConfig();
        configuration.register(MultiPartFeature.class);
        // configuration.register(JacksonFeature.class);
        restClient = ClientBuilder.newClient(configuration);
    }

    protected WebTarget target() {
        return restClient.target(url);
    }

    protected Cookie getUserCookie(String userId, char[] password) throws ServiceException {
        RestAuthenticationService authenticationClient = new RestAuthenticationService(url);
        return authenticationClient.getEsDmsCookie(new CredentialImpl.Builder().username(userId).password(password).build());
    }

    protected Cookie newUserCookie(String token) {
        checkNotNull(token);
        return new Cookie(ES_DMS_TICKET, token);
    }

}
