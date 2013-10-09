package com.github.richardwilly98.esdms.client;

import java.util.Collection;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.services.RoleService;

public class RestRoleServiceClientTest extends RestClientBaseTest {

    RestRoleServiceClientTest() throws Exception {
        super();
    }

    @Test
    public void testFindRoleById() {
        log.debug("*** testFindRoleById ***");
        try {
            String token = loginAsAdmin();
            Role role = getRestRoleServiceClient().findRoleById(token, RoleService.DefaultRoles.PROCESS_ADMINISTRATOR.getRole().getId());
            Assert.assertNotNull(role);
            Assert.assertEquals(role.getId(), RoleService.DefaultRoles.PROCESS_ADMINISTRATOR.getRole().getId());
        } catch (Throwable t) {
            log.error("testFindRoleById failed", t);
            Assert.fail("testFindRoleById failed");
        }
    }

    @Test
    public void testFindRolesByType() {
        log.debug("*** testFindRolesByType ***");
        try {
            String token = loginAsAdmin();
            Collection<Role> roles = getRestRoleServiceClient().findRolesByType(token, RoleType.SYSTEM);
            Assert.assertNotNull(roles);
            Assert.assertTrue(roles.size() > 0);
        } catch (Throwable t) {
            log.error("testFindRolesByType failed", t);
            Assert.fail("testFindRolesByType failed");
        }
    }
}
