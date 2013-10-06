package com.github.richardwilly98.esdms.services;

/*
 * #%L
 * es-dms-core
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

import static com.google.common.collect.Sets.newHashSet;

import com.github.richardwilly98.esdms.PermissionImpl;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.User;
import com.google.common.collect.ImmutableSet;

public interface UserService extends BaseService<User> {

    public enum DefaultUsers {
        ADMINISTRATOR(new UserImpl.Builder()
                .id(DEFAULT_ADMIN_LOGIN)
                .name(DEFAULT_ADMIN_LOGIN)
                .description(DEFAULT_ADMIN_DESCRIPTION)
                .email(DEFAULT_ADMIN_LOGIN)
                .password(DEFAULT_ADMIN_PASSWORD.toCharArray())
                .roles(newHashSet(ImmutableSet.of(RoleService.DefaultRoles.ADMINISTRATOR.getRole(),
                        RoleService.DefaultRoles.PROCESS_ADMIN.getRole()))).build());

        private final User user;

        DefaultUsers(final User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    public enum UserPermissions {
        CREATE_PERMISSION(new PermissionImpl.Builder().id(Constants.USER_CREATE).build()), EDIT_PERMISSION(new PermissionImpl.Builder().id(
                Constants.USER_EDIT).build()), DELETE_PERMISSION(new PermissionImpl.Builder().id(Constants.USER_DELETE).build()), ADD_PERMISSION(
                new PermissionImpl.Builder().id(Constants.USER_ADD).build()), REMOVE_PERMISSION(new PermissionImpl.Builder().id(
                Constants.USER_REMOVE).build());
        private final Permission permission;

        UserPermissions(final Permission permission) {
            this.permission = permission;
        }

        public Permission getPermission() {
            return permission;
        }

        public static class Constants {
            public static final String USER_CREATE = "user:create";
            public static final String USER_EDIT = "user:edit";
            public static final String USER_DELETE = "user:delete";
            public static final String USER_ADD = "user:add";
            public static final String USER_REMOVE = "user:remove";
        }
    }

    public static final String DEFAULT_ADMIN_DESCRIPTION = "System administrator";
    public static final String DEFAULT_ADMIN_LOGIN = "admin";
    public static final String DEFAULT_ADMIN_PASSWORD = "secret";
}
