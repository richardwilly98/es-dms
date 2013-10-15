package com.github.richardwilly98;

import org.testng.Assert;

import com.github.richardwilly98.activiti.EsDmsServerWithActivitiBase;
import com.github.richardwilly98.esdms.RoleImpl;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.client.RestAuthenticationService;
import com.github.richardwilly98.esdms.client.RestRoleService;
import com.github.richardwilly98.esdms.client.RestUserService;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.TestRestServerBase;


public class TestActivitiIdentityServiceBase extends EsDmsServerWithActivitiBase {

    final RestUserService restUserService;
    final RestAuthenticationService restAuthenticationService;
    final RestRoleService restRoleService;

    public TestActivitiIdentityServiceBase() throws Exception {
        super();
        restUserService = new RestUserService(TestRestServerBase.URL);
        restAuthenticationService = new RestAuthenticationService(TestRestServerBase.URL);
        restRoleService = new RestRoleService(TestRestServerBase.URL);
    }

    protected void deleteUser(com.github.richardwilly98.esdms.api.User user) throws ServiceException {
        Assert.assertNotNull(user);
        restUserService.delete(adminToken, user);
    }

    protected void deleteGroup(com.github.richardwilly98.esdms.api.Role role) throws ServiceException {
        Assert.assertNotNull(role);
        restRoleService.delete(adminToken, role);
    }
    
    protected Role createGroup(String id, RoleType type) throws ServiceException {
        Role role = new RoleImpl.Builder().id(id).name(id).type(type).build();
        return restRoleService.create(adminToken, role);
    }

}
