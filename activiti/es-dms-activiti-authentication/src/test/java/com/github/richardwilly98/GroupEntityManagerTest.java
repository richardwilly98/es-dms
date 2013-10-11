package com.github.richardwilly98;

import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.activiti.identity.GroupEntityManager;
import com.github.richardwilly98.esdms.RoleImpl;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class GroupEntityManagerTest extends TestActivitiIdentityServiceBase {

    public GroupEntityManagerTest() throws Exception {
        super();
    }

    @Test
    public void testFindGroupById() {
        log.debug("*** testFindGroupById ***");
        try {
            GroupQuery query = identityService.createGroupQuery().groupId(RoleService.DefaultRoles.Constants.ADMINISTRATOR_ROLE_ID);
            Assert.assertEquals(query.list().size(), 0);

            String id = "bpm-role-" + System.currentTimeMillis();
            query = identityService.createGroupQuery().groupId(id);
            Assert.assertEquals(query.list().size(), 0);

            Role role = new RoleImpl.Builder().id(id).name(id).type(RoleType.PROCESS).build();
            restRoleService.create(adminToken, role);
            query = identityService.createGroupQuery().groupId(id);
            Assert.assertEquals(query.list().size(), 1);
            deleteGroup(role);

            role = new RoleImpl.Builder().id(id).name(id).type(RoleType.USER_DEFINED).build();
            restRoleService.create(adminToken, role);
            query = identityService.createGroupQuery().groupId(id);
            Assert.assertEquals(query.list().size(), 0);
            deleteGroup(role);
        } catch (Throwable t) {
            Assert.fail("testFindGroupById failed", t);
        }
    }

    @Test
    public void testFindGroupByName() {
        log.debug("*** testFindGroupByName ***");
        try {
            GroupQuery query = identityService.createGroupQuery().groupName("Administrator");
            Assert.assertEquals(query.list().size(), 0);

            String name = "bpm-role-" + System.currentTimeMillis();
            query = identityService.createGroupQuery().groupName(name);
            Assert.assertEquals(query.list().size(), 0);

            Role role = new RoleImpl.Builder().id(name).name(name).type(RoleType.PROCESS).build();
            restRoleService.create(adminToken, role);
            query = identityService.createGroupQuery().groupName(name);
            Assert.assertEquals(query.list().size(), 1);
            deleteGroup(role);

            role = new RoleImpl.Builder().id(name).name(name).type(RoleType.USER_DEFINED).build();
            restRoleService.create(adminToken, role);
            query = identityService.createGroupQuery().groupName(name);
            Assert.assertEquals(query.list().size(), 0);
            deleteGroup(role);
        } catch (Throwable t) {
            Assert.fail("testFindGroupByName failed", t);
        }
    }

    @Test
    public void testFindGroupList() {
        log.debug("*** testFindGroupList ***");
        try {
            List<Group> groups = identityService.createGroupQuery().list();
            Assert.assertEquals(groups.size(), 0);

            String name = "bpm-role-" + System.currentTimeMillis();
            String name2 = "bpm-role2-" + System.currentTimeMillis();

            Role role = new RoleImpl.Builder().id(name).name(name).type(RoleType.PROCESS).build();
            restRoleService.create(adminToken, role);
            groups = identityService.createGroupQuery().list();
            Assert.assertEquals(groups.size(), 1);

            Role role2 = new RoleImpl.Builder().id(name2).name(name2).type(RoleType.USER_DEFINED).build();
            restRoleService.create(adminToken, role2);
            groups = identityService.createGroupQuery().list();
            Assert.assertEquals(groups.size(), 1);

            deleteGroup(role);
            deleteGroup(role2);
            groups = identityService.createGroupQuery().list();
            Assert.assertEquals(groups.size(), 0);
        } catch (Throwable t) {
            Assert.fail("testFindGroupList failed", t);
        }
    }

    @Test
    public void testGroupMember() {
        log.debug("*** testGroupMember ***");
        try {
            GroupQuery query = identityService.createGroupQuery().groupMember(UserService.DEFAULT_ADMIN_LOGIN);
            Assert.assertEquals(query.list().size(), 1);

            Optional<Group> groupAdmin = Iterables.tryFind(query.list(), new Predicate<Group>() {
                public boolean apply(Group group) {
                    log.debug("Check role id: " + group.getId());
                    return group.getId().equals(GroupEntityManager.ADMIN_GROUP_ID)
                            && group.getName().equals(GroupEntityManager.ADMIN_GROUP_NAME)
                            && group.getType().equals(GroupEntityManager.ADMIN_GROUP_TYPE);
                }
            });
            Assert.assertTrue(groupAdmin.isPresent());

            String name = "bpm-role-" + System.currentTimeMillis();
            Role role = new RoleImpl.Builder().id(name).name(name).type(RoleType.PROCESS).build();
            restRoleService.create(adminToken, role);

            String login = "bpm-user-" + System.currentTimeMillis() + "@activiti";
            com.github.richardwilly98.esdms.api.User user = createUser(login, login, ImmutableSet.of(role));
            query = identityService.createGroupQuery().groupMember(login);
            Assert.assertEquals(query.list().size(), 1);

            String name2 = "bpm2-role-" + System.currentTimeMillis();
            Role role2 = new RoleImpl.Builder().id(name2).name(name2).type(RoleType.PROCESS).build();
            restRoleService.create(adminToken, role2);

            String login2 = "bpm2-user-" + System.currentTimeMillis() + "@activiti";
            com.github.richardwilly98.esdms.api.User user2 = createUser(login2, login2, ImmutableSet.of(role, role2));
            query = identityService.createGroupQuery().groupMember(login2);
            Assert.assertEquals(query.list().size(), 2);

            deleteUser(user);
            deleteUser(user2);
            deleteGroup(role);
            deleteGroup(role2);
        } catch (Throwable t) {
            Assert.fail("testGroupMember failed", t);
        }
    }

    @Deployment(resources = { "org/activiti/test/test-candidate-process.bpmn20.xml" })
    @Test
    public void testPotentialStarter() {
        log.debug("*** testPotentialStarter ***");
        try {
            String name = "bpm-role-" + System.currentTimeMillis();
            Role role = new RoleImpl.Builder().id(name).name(name).type(RoleType.PROCESS).build();
            restRoleService.create(adminToken, role);

            Role sales = new RoleImpl.Builder().id("sales").name("sales").type(RoleType.PROCESS).build();
            restRoleService.create(adminToken, sales);

            String login = "bpm-user-candidate@activiti";
            com.github.richardwilly98.esdms.api.User user = createUser(login, login, ImmutableSet.of(role));

            String bossLogin = "bpm-vp-sales-" + System.currentTimeMillis() + "@activiti";
            com.github.richardwilly98.esdms.api.User boosUser = createUser(bossLogin, bossLogin, ImmutableSet.of(sales));

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testCandidateGroup");
            Assert.assertNotNull(processInstance);

            Assert.assertEquals(1, taskService.createTaskQuery().taskCandidateGroup("sales").count());

            // bossLogin is a member of the candidate group and should be able
            // to find the task
            Assert.assertEquals(1, taskService.createTaskQuery().taskCandidateUser(bossLogin).count());

            // bpm-user-candidate@activiti is a candidate user and should be
            // able to find the task
            Assert.assertEquals(1, taskService.createTaskQuery().taskCandidateUser(login).count());

            deleteGroup(role);
            deleteGroup(sales);
            deleteUser(user);
            deleteUser(boosUser);
        } catch (Throwable t) {
            Assert.fail("testGroupMember failed", t);
        }
    }

}
