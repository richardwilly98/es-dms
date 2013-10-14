package com.github.richardwilly98.activiti.explorer.login;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.activiti.explorer.identity.LoggedInUser;
import org.activiti.explorer.identity.LoggedInUserImpl;
import org.activiti.explorer.ui.login.DefaultLoginHandler;
import org.apache.log4j.Logger;

import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.client.RestAuthenticationService;
import com.github.richardwilly98.esdms.exception.ServiceException;

public class EsDmsLoginHandler extends DefaultLoginHandler {

    /**
     * 
     */
    private static final long serialVersionUID = 2159332213746178784L;

    private static final Logger log = Logger.getLogger(EsDmsLoginHandler.class);

    private transient RestAuthenticationService restAuthenticationClient;
    private String url;
    private String userId;
    private String password;
    private transient IdentityService identityService;
    
    private RestAuthenticationService getRestAuthenticationClient() {
        if (restAuthenticationClient == null) {
            restAuthenticationClient = new RestAuthenticationService(url);
        }
        return restAuthenticationClient;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
      }
    
    @Override
    public LoggedInUser authenticate(HttpServletRequest request, HttpServletResponse response) {
        log.debug(String.format("authenticate: %s", request));
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                log.debug("Cookie in request:" + cookie.getName() + " - " + cookie.getValue());
                if (RestAuthenticationService.ES_DMS_TICKET.equals(cookie.getName())) {
                    try {
                        User user = getRestAuthenticationClient().validate(cookie.getValue());
                        if (user == null) {
                            throw new ServiceException(String.format("Cannot get user from %s", cookie));
                        }
                        EsDmsLoggedInUser loggedInUser = new EsDmsLoggedInUser(identityService);
                        loggedInUser.convert(user);
                        return loggedInUser;
                    } catch (ServiceException sEx) {
                        log.error("Validate token failed.", sEx);
                    }
                }
            }
        }
        return super.authenticate(request, response);
    }

    @Override
    public LoggedInUserImpl authenticate(String userName, String password) {
        log.debug(String.format("authenticate: %s - %s", userName, password));
        return super.authenticate(userName, password);
    }

    // TODO: Should logout method delete ES_DMS_TICKET cookie?
    @Override
    public void logout(LoggedInUser userToLogout) {
        log.debug(String.format("logout: %s", userToLogout));
        super.logout(userToLogout);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
