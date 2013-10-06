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

import com.github.richardwilly98.esdms.PermissionImpl;
import com.github.richardwilly98.esdms.RoleImpl;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.services.DocumentService.DocumentPermissions;
import com.github.richardwilly98.esdms.services.UserService.UserPermissions;
import com.google.common.collect.ImmutableSet;

public interface RoleService extends BaseService<Role> {

    public enum DefaultRoles {
        ADMINISTRATOR(new RoleImpl.Builder()
                .id("administrator")
                .name("Administrator")
                .type(RoleType.SYSTEM)
                .permissions(
                        ImmutableSet.of(DocumentPermissions.READ_PERMISSION.getPermission(),
                                DocumentPermissions.EDIT_PERMISSION.getPermission(), DocumentPermissions.CREATE_PERMISSION.getPermission(),
                                DocumentPermissions.DELETE_PERMISSION.getPermission(), UserPermissions.EDIT_PERMISSION.getPermission(),
                                UserPermissions.CREATE_PERMISSION.getPermission(), UserPermissions.DELETE_PERMISSION.getPermission(),
                                RolePermissions.EDIT_PERMISSION.getPermission(), RolePermissions.CREATE_PERMISSION.getPermission(),
                                RolePermissions.DELETE_PERMISSION.getPermission())).build()), WRITER(new RoleImpl.Builder()
                .id("writer")
                .name("Writer")
                .type(RoleType.SYSTEM)
                .permissions(
                        ImmutableSet.of(DocumentPermissions.READ_PERMISSION.getPermission(),
                                DocumentPermissions.EDIT_PERMISSION.getPermission(), DocumentPermissions.CREATE_PERMISSION.getPermission(),
                                DocumentPermissions.DELETE_PERMISSION.getPermission())).build()), READER(new RoleImpl.Builder()
                .id("reader").name("Reader").type(RoleType.SYSTEM)
                .permissions(ImmutableSet.of(DocumentPermissions.READ_PERMISSION.getPermission())).build()), PROCESS_USER(
                new RoleImpl.Builder().id("process-user").name("Process User").type(RoleType.SYSTEM).build()), PROCESS_ADMIN(
                new RoleImpl.Builder().id("process-admin").name("Process Administrator").type(RoleType.SYSTEM).build()),
                DEFAULT(new RoleImpl.Builder()
                .id("reader").name("Reader").type(RoleType.SYSTEM)
                .permissions(ImmutableSet.of(DocumentPermissions.READ_PERMISSION.getPermission())).build());

        private Role role;

        DefaultRoles(Role role) {
            this.role = role;
        }

        public Role getRole() {
            return role;
        }
    }

    public enum RolePermissions {
        CREATE_PERMISSION(new PermissionImpl.Builder().id(Constants.ROLE_CREATE).build()), EDIT_PERMISSION(new PermissionImpl.Builder().id(
                Constants.ROLE_EDIT).build()), DELETE_PERMISSION(new PermissionImpl.Builder().id(Constants.ROLE_DELETE).build()), ADD_PERMISSION(
                new PermissionImpl.Builder().id(Constants.ROLE_ADD).build()), REMOVE_PERMISSION(new PermissionImpl.Builder().id(
                Constants.ROLE_REMOVE).build());
        private Permission permission;

        RolePermissions(Permission permission) {
            this.permission = permission;
        }

        public Permission getPermission() {
            return permission;
        }

        public static class Constants {
            public static final String ROLE_CREATE = "role:create";
            public static final String ROLE_EDIT = "role:edit";
            public static final String ROLE_DELETE = "role:delete";
            public static final String ROLE_ADD = "role:add";
            public static final String ROLE_REMOVE = "role:remove";
        }
    }

}
