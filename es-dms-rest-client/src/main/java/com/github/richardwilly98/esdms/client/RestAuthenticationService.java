package com.github.richardwilly98.esdms.client;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;

public class RestAuthenticationService extends RestClientBase {

    public static final String AUTH_PATH = "auth";
    public static final String LOGOUT_PATH = "logout";
    public static final String LOGIN_PATH = "login";
    private static final String VALIDATE_PATH = "validate";

    public RestAuthenticationService(final String url) {
        super(url, AUTH_PATH);
    }

    Cookie getEsDmsCookie(Credential credential) throws ServiceException {
        Response response = target().path(LOGIN_PATH).request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(credential, MediaType.APPLICATION_JSON));
        log.debug("status: " + response.getStatus());
        if (response.getStatus() == Status.OK.getStatusCode()) {
            for (NewCookie cookie : response.getCookies().values()) {
                if (ES_DMS_TICKET.equals(cookie.getName())) {
                    return new Cookie(cookie.getName(), cookie.getValue());
                }
            }
        } else {
            throw new ServiceException(String.format("login with user %s failed. %s", credential.getUsername(), response.getStatus()));
        }
        return null;
    }

    public String login(Credential credential) throws ServiceException {
        Response response = target().path(LOGIN_PATH).request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(credential, MediaType.APPLICATION_JSON_TYPE));
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new ServiceException(String.format("login failed for %s", credential.getUsername()));
        } else {
            AuthenticationResponse ar = response.readEntity(AuthenticationResponse.class);
            return ar.getToken();
        }
    }

    public void logout(String token) throws ServiceException {
        checkNotNull(token);
        Cookie cookie = new Cookie(ES_DMS_TICKET, token);
        Response response = target().path(LOGOUT_PATH).request().cookie(cookie).post(Entity.json(null));
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new ServiceException(String.format("logout failed for %s", token));
        }
    }

    public User validate(String token) throws ServiceException {
        checkNotNull(token);
        Cookie cookie = new Cookie(ES_DMS_TICKET, token);
        Response response = target().path(VALIDATE_PATH).request(MediaType.APPLICATION_JSON).cookie(cookie).post(Entity.json(null));
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new ServiceException(String.format("validate failed for %s", token));
        }
        
        ItemResponse item = response.readEntity(ItemResponse.class);
        response = restClient.target(item.getUrl()).request(MediaType.APPLICATION_JSON).cookie(cookie).get();
        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new ServiceException(String.format("Cannot get user from uri %s - status %s", item, response.getStatus()));
        }
        User user = response.readEntity(User.class);
        return user;
    }

    @SuppressWarnings("unused")
    private static class AuthenticationResponse {
        private String status;
        private String token;

        public AuthenticationResponse() {
        }

        public String getToken() {
            return token;
        }

        public String getStatus() {
            return status;
        }
    }

    @SuppressWarnings("unused")
    private static class ItemResponse {
        private String url;
        private String id;

        public ItemResponse() {}
        
        public String getUrl() {
            return url;
        }

        public String getId() {
            return id;
        }
    }
}
