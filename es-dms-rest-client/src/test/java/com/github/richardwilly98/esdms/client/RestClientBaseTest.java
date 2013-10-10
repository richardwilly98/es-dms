package com.github.richardwilly98.esdms.client;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.TestRestServerBase;
import com.github.richardwilly98.esdms.services.UserService;

public abstract class RestClientBaseTest extends TestRestServerBase {

    private RestAuthenticationService restAuthenticationServiceClient;
    private RestUserService restUserServiceClient;
    private RestRoleService restRoleServiceClient;
    private final String url;

    RestClientBaseTest() throws Exception {
        super();
        url = getBaseURI(false).toString();
        log.debug("getBaseURI: " + url);
    }

    protected RestAuthenticationService getRestAuthenticationServiceClient() {
        if (restAuthenticationServiceClient == null) {
            restAuthenticationServiceClient = new RestAuthenticationService(url);
        }
        return restAuthenticationServiceClient;
    }

    protected RestUserService getRestUserServiceClient() {
        if (restUserServiceClient == null) {
            restUserServiceClient = new RestUserService(url);
        }
        return restUserServiceClient;
    }

    protected RestRoleService getRestRoleServiceClient() {
        if (restRoleServiceClient == null) {
            restRoleServiceClient = new RestRoleService(url);
        }
        return restRoleServiceClient;
    }

    protected String loginAsAdmin() throws ServiceException {
        return getRestAuthenticationServiceClient().login(
                new CredentialImpl.Builder().username(UserService.DEFAULT_ADMIN_LOGIN)
                        .password(UserService.DEFAULT_ADMIN_PASSWORD.toCharArray()).build());
    }

}
