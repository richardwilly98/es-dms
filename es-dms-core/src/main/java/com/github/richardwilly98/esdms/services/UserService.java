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

import com.github.richardwilly98.esdms.api.User;

public interface UserService extends BaseService<User> {

    public static final String CREATE_PERMISSION = "user:create";
    public static final String EDIT_PERMISSION = "user:edit";
    public static final String DELETE_PERMISSION = "user:delete";

    public static final String ADD_PERMISSION = "user:add";
    public static final String REMOVE_PERMISSION = "user:remove";

    public static final String DEFAULT_ADMIN_DESCRIPTION = "System administrator";
    public static final String DEFAULT_ADMIN_LOGIN = "admin";
    public static final String DEFAULT_ADMIN_PASSWORD = "secret";
}
