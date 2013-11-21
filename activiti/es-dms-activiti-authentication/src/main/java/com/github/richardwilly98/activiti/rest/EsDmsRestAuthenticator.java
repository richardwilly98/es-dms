package com.github.richardwilly98.activiti.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.rest.common.filter.RestAuthenticator;
import org.apache.log4j.Logger;
import org.restlet.Request;
import org.restlet.data.Cookie;
import org.restlet.security.User;

import com.github.richardwilly98.activiti.identity.UserEntityManager;
import com.github.richardwilly98.esdms.client.RestAuthenticationService;
import com.github.richardwilly98.esdms.exception.ServiceException;

public class EsDmsRestAuthenticator implements RestAuthenticator {

    static final String ES_DMS_PROPERTIES = "es-dms.properties";
    static final String ESDMS_URL = "esdms.url";

    private static final Logger log = Logger.getLogger(EsDmsRestAuthenticator.class);

    private transient RestAuthenticationService restAuthenticationClient;
    private String url = "http://localhost:8080/api";

    public EsDmsRestAuthenticator() {
        super();
        loadProperties();
        log.info("url:" + url);
    }

    private void loadProperties() {
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(ES_DMS_PROPERTIES);
            if (stream == null) {
                log.warn("Cannot load properties from es-dms.properties");
            }
            Properties props = new Properties();
            props.load(stream);
            url = props.getProperty(ESDMS_URL);
        } catch (IOException ex) {
            log.error("loadProperties failed.", ex);
        }

    }

    private RestAuthenticationService getRestAuthenticationClient() {
        if (restAuthenticationClient == null) {
            log.debug("Instantiate RestAuthenticationService with url: " + url);
            restAuthenticationClient = new RestAuthenticationService(url);
        }
        return restAuthenticationClient;
    }

    @Override
    public boolean requestRequiresAuthentication(Request request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            setUserInfo(request, token);
        }
        return true;
    }

    private void setUserInfo(Request request, String token) {
        com.github.richardwilly98.esdms.api.User user;
        try {
            user = getRestAuthenticationClient().validate(token);
            if (user == null) {
                throw new ServiceException(String.format("Cannot get user from token %s", token));
            }
            UserEntity userEntity = UserEntityManager.convertToUserEntity(user);
            User restletUser = new User(userEntity.getId());
            restletUser.setEmail(userEntity.getEmail());
            restletUser.setFirstName(userEntity.getFirstName());
            restletUser.setLastName(userEntity.getLastName());
            request.getClientInfo().setUser(restletUser);
            request.getClientInfo().setAuthenticated(true);
        } catch (ServiceException ex) {
            log.warn("setUserInfo failed", ex);
        }
    }

    @Override
    public boolean isRequestAuthorized(Request request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            setUserInfo(request, token);
        }
        return true;
    }

    private String extractTokenFromRequest(Request request) {
        String token = null;
        if (request != null) {
            log.debug(request);
            if (request.getResourceRef() != null && request.getResourceRef().getQueryAsForm() != null) {
                token = request.getResourceRef().getQueryAsForm().getFirstValue("token");
                log.trace("Found token from query string: " + token);
            } else {
                log.info("ResourceRef is null");
            }
            for (Cookie cookie : request.getCookies()) {
                log.trace(cookie.getName() + " - " + cookie.getValue());
                if (RestAuthenticationService.ES_DMS_TICKET.equals(cookie.getName())) {
                    token = cookie.getValue();
                    log.trace("Found token from cookie: " + token);
                    break;
                }
            }
        }
        return token;
    }

}
