package com.github.richardwilly98;

import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.spring.impl.test.SpringActivitiTestCase;
import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration("classpath:activiti-context.xml")
public class UserEntityManagerTest extends SpringActivitiTestCase {

    private static final Logger log = Logger.getLogger(UserEntityManagerTest.class);

    public void testCheckPassword() {
        log.debug("*** checkPassword ***");
        boolean isAuthenticated = identityService.checkPassword("kermit", "kermit");
        assertFalse(isAuthenticated);
        isAuthenticated = identityService.checkPassword("admin", "secret");
        assertTrue(isAuthenticated);
    }

    public void testFindUserById() {
        log.debug("*** findUserById ***");
        UserQuery query = identityService.createUserQuery().userId("kermit-" + System.currentTimeMillis());
        assertTrue(query.list().size() == 0);

        query = identityService.createUserQuery().userId("admin");
        assertTrue(query.list().size() > 0);
        log.debug("query count: " + query.list().size());
        User user = query.singleResult();
        assertNotNull(user);
        log.debug(user);

        query = identityService.createUserQuery().userId("testbpm@gmail.com");
        assertTrue(query.list().size() > 0);
        log.debug("query count: " + query.list().size());
        user = query.singleResult();
        assertNotNull(user);
        log.debug(user);
    }

    public void testFindUserByEmail() {
        log.debug("*** testFindUserByEmail ***");
        UserQuery query = identityService.createUserQuery().userEmail("kermit-" + System.currentTimeMillis());
        assertTrue(query.list().size() == 0);

        query = identityService.createUserQuery().userEmail("admin");
        assertTrue(query.list().size() > 0);
        log.debug("query count: " + query.list().size());
        User user = query.singleResult();
        assertNotNull(user);
        assertEquals(user.getEmail(), "admin");
        log.debug(user);
    }

    public void testFindUserByLastName() {
        log.debug("*** testFindUserByLastName ***");
        UserQuery query = identityService.createUserQuery().userLastName("kermit-" + System.currentTimeMillis());
        assertTrue(query.list().size() == 0);

        query = identityService.createUserQuery().userLastName("admin");
        assertTrue(query.list().size() > 0);
        log.debug("query count: " + query.list().size());
        User user = query.singleResult();
        assertNotNull(user);
        assertEquals(user.getLastName(), "admin");
    }

    public void testFindUsersById() {
        log.debug("*** testFindUsersById ***");
        UserQuery query = identityService.createUserQuery().userId("user*");
        log.debug(query.list().size());
        if (query.list().size() > 0) {
            for (User user : query.list()) {
                assertTrue(user.getId().contains("user"));
            }
        }

        // query = identityService.createUserQuery().userId("admin");
        // assertTrue(query.list().size() > 0);
        // log.debug("query count: " + query.list().size());
        // User user = query.singleResult();
        // assertNotNull(user);
        // log.debug(user);
    }
}
