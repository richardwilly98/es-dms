package test.github.richardwilly98.esdms.services;

/*
 * #%L
 * es-dms-service
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.elasticsearch.common.io.Streams.copyToBytesFromClasspath;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Guice;

import test.github.richardwilly98.esdms.inject.ProviderModule;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.PermissionImpl;
import com.github.richardwilly98.esdms.RoleImpl;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.VersionImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Document.DocumentStatus;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.api.Session;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.AuditService;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.DocumentService;
import com.github.richardwilly98.esdms.services.PermissionService;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.SearchService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/*
 * Base class for all test providers
 */
@Guice(modules = ProviderModule.class)
public class ProviderTestBase {

    final protected Logger log = Logger.getLogger(this.getClass());
    final static Map<String, User> users = newHashMap();
    final static Set<Permission> permissions = newHashSet();
    final static Set<Role> roles = newHashSet();
    final static Role readerRole = RoleService.DefaultRoles.READER.getRole();
    final static Role writerRole = RoleService.DefaultRoles.WRITER.getRole();
    final static Role collaboratorRole;
    // final static Permission createDocumentPermission;
    // final static Permission deleteDocumentPermission;
    // final static Permission readDocumentPermission;
    static String adminToken;

    static {
        // createDocumentPermission = new
        // PermissionImpl.Builder().name("document:create").description("Create document").build();
        permissions.add(DocumentService.DocumentPermissions.CREATE_PERMISSION.getPermission());

        // deleteDocumentPermission = new
        // PermissionImpl.Builder().name("document:delete").description("Delete document").build();
        permissions.add(DocumentService.DocumentPermissions.DELETE_PERMISSION.getPermission());

        // readDocumentPermission = new
        // PermissionImpl.Builder().name("document:read").description("Read document").build();
        permissions.add(DocumentService.DocumentPermissions.READ_PERMISSION.getPermission());

        collaboratorRole = new RoleImpl.Builder().id("collaborator").name("Collaborator").description("Collaborator").disabled(false)
                .permissions(permissions).build();
        roles.add(collaboratorRole);

        // readerRole = RoleService.DefaultRoles.READER.getRole();
        roles.add(readerRole);

        final User user = new UserImpl.Builder().id("richard.louapre@gmail.com").name("Richard").disabled(false).city("Jersey City")
                .password("secret".toCharArray()).email("richard.louapre@gmail.com").roles(ImmutableSet.of(readerRole)).build();
        // user.addRole(readerRole);
        users.put(user.getLogin(), user);

        final User user2 = new UserImpl.Builder().id("danilo.sandron@gmail.com").name("Danilo").disabled(false).city("Bankok")
                .password("segreto".toCharArray()).email("danilo.sandron@gmail.com").roles(ImmutableSet.of(collaboratorRole)).build();
        // user.addRole(collaboratorRole);
        users.put(user2.getLogin(), user2);
    }

    @Inject
    Client client;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    AuditService auditService;

    @Inject
    UserService userService;

    @Inject
    DocumentService documentService;

    @Inject
    SearchService<Document> searchService;

    @Inject
    RoleService roleService;

    @Inject
    PermissionService permissionService;

    @BeforeSuite
    public void beforeSuite() {
        log.info("** beforeSuite **");
        loginAdminUser();
        createPermissions();
        createRoles();
        createUsers();
    }

    @AfterSuite
    public void tearDownSuite() throws Exception {
        log.info("*** tearDownSuite ***");
        tearDownElasticsearch();
    }

    private void tearDownElasticsearch() throws Exception {
        log.info("*** tearDownElasticsearch ***");
        client.admin().indices().prepareDelete().execute().actionGet();
        client.close();
    }

    protected void loginAdminUser() {
        try {
            adminToken = authenticationService.login(new CredentialImpl.Builder().username(UserService.DEFAULT_ADMIN_LOGIN)
                    .password(UserService.DEFAULT_ADMIN_PASSWORD.toCharArray()).build());
        } catch (ServiceException ex) {
            log.error("loginAdminUser failed", ex);
            Assert.fail("loginAdminUser failed", ex);
        }
    }

    protected String login(User user) {
        try {
            checkNotNull(user);
            // Must generate a copy of password char[]. It will be reset to null
            // by Shiro
            Credential credential = new CredentialImpl.Builder().password(Arrays.copyOf(user.getPassword(), user.getPassword().length))
                    .username(user.getLogin()).build();
            return authenticationService.login(credential);
        } catch (ServiceException ex) {
            log.error("login failed", ex);
            Assert.fail("login failed", ex);
        }
        return null;
    }

    protected Session getSession(String token) {
        checkNotNull(token);
        try {
            return authenticationService.get(token);
        } catch (ServiceException ex) {
            log.error("getSession failed", ex);
            Assert.fail("getSession failed", ex);
        }
        return null;
    }

    protected boolean hasRole(String token, Role role) {
        checkNotNull(token);
        checkNotNull(role);
        try {
            return authenticationService.hasRole(token, role.getId());
        } catch (ServiceException ex) {
            log.error("hasRole failed", ex);
            Assert.fail("hasRole failed", ex);
        }
        return false;
    }

    protected boolean hasPermission(String token, Permission permission) {
        checkNotNull(token);
        checkNotNull(permission);
        try {
            return authenticationService.hasPermission(token, permission.getId());
        } catch (ServiceException ex) {
            log.error("hasPermission failed", ex);
            Assert.fail("hasPermission failed", ex);
        }
        return false;
    }

    protected void logout(String token) {
        checkNotNull(token);
        try {
            authenticationService.logout(token);
        } catch (ServiceException ex) {
            log.error("logout failed", ex);
            Assert.fail("logout failed", ex);
        }
    }

    private void createUsers() {
        try {
            for (User user : users.values()) {
                createUser(user);
            }
        } catch (ServiceException ex) {
            log.error("createUsers failed", ex);
            Assert.fail("createUsers failed", ex);
        }
    }

    private void createRoles() {
        try {
            for (Role role : roles) {
                createRole(role);
            }
        } catch (ServiceException ex) {
            log.error("createRoles failed", ex);
            Assert.fail("createRoles failed", ex);
        }
    }

    private void createPermissions() {
        try {
            for (Permission permission : permissions) {
                createPermission(permission);
            }
        } catch (ServiceException ex) {
            log.error("createPermissions failed", ex);
            Assert.fail("createPermissions failed", ex);
        }
    }

    @BeforeClass
    public void setupServer() {
        log.info("** setupServer **");
    }

    @AfterClass
    public void closeServer() {
        log.info("** closeServer **");
    }

    protected Permission createPermission(Permission permission) throws ServiceException {
        try {
            Permission newPermission = permissionService.create(permission);
            Assert.assertNotNull(newPermission);
            Assert.assertEquals(permission.getId(), newPermission.getId());
            return newPermission;
        } catch (ServiceException e) {
            log.error("createPermission failed", e);
            throw e;
        }
    }

    protected Permission createPermission(String name, String description, boolean disabled) throws ServiceException {
        Assert.assertTrue(!(name == null || name.isEmpty()));

        Permission permission = new PermissionImpl.Builder().id(name).name(name).description(description).disabled(disabled).build();
        return createPermission(permission);
    }

    protected User createUser(User user) throws ServiceException {
        try {
            User newUser = userService.create(user);
            Assert.assertNotNull(newUser);
            Assert.assertEquals(user.getId(), newUser.getId());
            return newUser;
        } catch (ServiceException e) {
            log.error("createUser failed", e);
            throw e;
        }
    }

    protected User createUser(String name, String description, boolean disabled, String email, char[] password, Set<Role> roles)
            throws ServiceException {
        User user = new UserImpl.Builder().id(email).name(name).description(description).disabled(disabled).email(email).password(password)
                .roles(roles).build();
        return createUser(user);
    }

    protected Role createRole(Role role) throws ServiceException {
        try {
            Role newRole = roleService.create(role);
            Assert.assertNotNull(newRole);
            Assert.assertEquals(role.getId(), newRole.getId());
            return newRole;
        } catch (ServiceException e) {
            log.error("createRole failed", e);
            throw e;
        }
    }

    protected Role createRole(String name, String description, boolean disabled, Set<Permission> permissions) throws ServiceException {
        return createRole(name, description, disabled, null, permissions);
    }

    protected Role createRole(String name, String description, boolean disabled, RoleType type, Set<Permission> permissions)
            throws ServiceException {
        log.trace("Preparing to create permission: " + name);
        Assert.assertTrue(!(name == null || name.isEmpty()));
        Role role = new RoleImpl.Builder().id(name).name(name).description(description).disabled(disabled).type(type)
                .permissions(permissions).build();
        return createRole(role);
    }

    protected String createDocument(String name, String contentType, String path) throws Throwable {
        String id = String.valueOf(System.currentTimeMillis());
        byte[] content = copyToBytesFromClasspath(path);
        File file = new FileImpl.Builder().content(content).name(name).contentType(contentType).build();
        Set<Version> versions = newHashSet();
        versions.add(new VersionImpl.Builder().documentId(id).file(file).current(true).versionId(1).build());

        Document document = new DocumentImpl.Builder().versions(versions).id(id).name(name).roles(null).build();
        Document newDocument = documentService.create(document);
        Assert.assertNotNull(newDocument);
        Assert.assertEquals(id, newDocument.getId());
        Assert.assertTrue(newDocument.hasStatus(DocumentStatus.AVAILABLE));
        log.info(String.format("New document created #%s", newDocument.getId()));
        return id;
    }

}
