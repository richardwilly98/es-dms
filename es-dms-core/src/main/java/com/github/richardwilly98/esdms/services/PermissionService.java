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

import com.github.richardwilly98.esdms.api.Permission;

public interface PermissionService extends BaseService<Permission> {

    public enum PermissionPermissions {
        CREATE_PERMISSION(Constants.PERMISSION_CREATE), EDIT_PERMISSION(Constants.PERMISSION_EDIT), DELETE_PERMISSION(
                Constants.PERMISSION_DELETE), ADD_PERMISSION(Constants.PERMISSION_ADD), REMOVE_PERMISSION(Constants.PERMISSION_REMOVE);
        private String id;

        PermissionPermissions(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static class Constants {
            public static final String PERMISSION_CREATE = "permission:create";
            public static final String PERMISSION_EDIT = "permission:edit";
            public static final String PERMISSION_DELETE = "permission:delete";
            public static final String PERMISSION_ADD = "permission:add";
            public static final String PERMISSION_REMOVE = "permission:remove";
        }
    }

}
