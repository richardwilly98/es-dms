package com.github.richardwilly98.esdms.services;

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

import static com.google.common.base.Preconditions.checkArgument;
import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.ByteSource;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.search.api.SearchResult;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

@Singleton
public class UserProvider extends ProviderItemBase<User> implements UserService {

    private static final String USER_MAPPING_JSON = "/com/github/richardwilly98/esdms/services/user-mapping.json";
    private final static String type = "user";
    private final HashService hashService;
    private final RoleService roleService;

    @Inject
    UserProvider(Client client, BootstrapService bootstrapService, HashService hashService, RoleService roleService)
            throws ServiceException {
        super(client, bootstrapService, null, UserProvider.type, User.class);
        this.hashService = hashService;
        this.roleService = roleService;
    }

    @Override
    protected void loadInitialData() throws ServiceException {
        User admin = UserService.DefaultUsers.ADMINISTRATOR.getUser();
        admin.setHash(computeBase64Hash(admin.getPassword()));
        super.create(admin);
    }

    @Override
    protected String getMapping() {
        try {
            return copyToStringFromClasspath(USER_MAPPING_JSON);
        } catch (IOException ioEx) {
            log.error("getMapping failed", ioEx);
            return null;
        }
    }

    private String computeBase64Hash(char[] password) {
        String hash = hashService.toBase64(ByteSource.Util.bytes(password).getBytes());
        if (log.isDebugEnabled()) {
            log.debug(String.format("computeBase64Hash - %s", hash));
        }
        return hash;
    }

    @RequiresPermissions(UserPermissions.Constants.USER_CREATE)
    @Override
    public User create(User user) throws ServiceException {
        try {
            if (! Strings.isNullOrEmpty(user.getLogin())) {
                User userByLogin = null;
                try {
                    userByLogin = findByLogin(user.getLogin());
                } catch (ServiceException sEx) {
                    
                } finally {
                    if (userByLogin != null) {
                        throw new ServiceException(String.format("A user already exists with same login: %s", user.getLogin()));
                    }
                }
            }
            if (user.getPassword() != null) {
                String encodedHash = computeBase64Hash(user.getPassword());
                if (log.isTraceEnabled()) {
                    log.trace(String.format("From service - hash: %s for login %s", encodedHash, user.getLogin()));
                }
                user.setHash(encodedHash);
                // user.setPassword(null);
            }

            if (user.getRoles().isEmpty()) {
                Role role = roleService.get(RoleService.DefaultRoles.DEFAULT.getRole().getId());
                if (role != null) {
                    user.addRole(role);
                } else {
                    throw new ServiceException(String.format("Could not find default role %s", RoleService.DefaultRoles.DEFAULT.getRole()
                            .getId()));
                }
            }

            User newUser = super.create(user);
            return newUser;
        } catch (Throwable t) {
            log.error(String.format("create failed - %s", t));
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    @RequiresPermissions(UserPermissions.Constants.USER_EDIT)
    @Override
    public User update(User user) throws ServiceException {
        checkArgument(canUserBeUpdated(user), String.format(
                "Cannot update user %s. Missing required system roles", user.getId()));
        if (! Strings.isNullOrEmpty(user.getLogin())) {
            User userByLogin = null;
            try {
                userByLogin = findByLogin(user.getLogin());
            } catch (ServiceException sEx) {
                
            } finally {
                if (userByLogin != null && !user.getId().equals(userByLogin.getId())) {
                    throw new ServiceException(String.format("A user already exists with same login: %s", user.getLogin()));
                }
            }
        }
        if (user.getPassword() != null) {
            String encodedHash = computeBase64Hash(user.getPassword());
            if (log.isTraceEnabled()) {
                log.trace(String.format("From service - hash: %s for login %s", encodedHash, user.getLogin()));
            }
            user.setHash(encodedHash);
            // item.setPassword(null);
        }
        return super.update(user);
    }

    @RequiresPermissions(UserPermissions.Constants.USER_DELETE)
    @Override
    public void delete(User item) throws ServiceException {
        checkArgument(!item.getId().equals(UserService.DEFAULT_ADMIN_LOGIN), String.format("Cannot delete %s user", item.getId()));
        super.delete(item);
    }

    @Override
    public User findByLogin(String login) throws ServiceException {
        QueryBuilder query = QueryBuilders.matchQuery("login", login);
        SearchResult<User> searchResult = search(query, 0, 1);
        if (searchResult.getTotalHits() == 1) {
            return Iterables.get( searchResult.getItems(), 0);
        }
        
        if (searchResult.getTotalHits() > 1) {
            if (log.isTraceEnabled()) {
                for (User user : searchResult.getItems()) {
                    log.warn("USER WITH SAME LOGIN: " + user);
                }
            }
            throw new ServiceException(String.format("Found more than one user with the same login %s. Possible data integrity issue", login));
        }
        throw new ServiceException(String.format("Cannot find user with login %s", login));
    }
    
    private boolean canUserBeUpdated(User user) {
        if (user.getId().equals(UserService.DEFAULT_ADMIN_LOGIN)) {
            for (Role role : RoleService.SystemRoles) {
                if (!user.getRoles().contains(role)) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    @Override
    protected void doStart() throws ServiceException {
    }

    @Override
    protected void doStop() throws ServiceException {
    }
}
