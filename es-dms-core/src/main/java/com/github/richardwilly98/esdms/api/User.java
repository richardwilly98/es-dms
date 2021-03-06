package com.github.richardwilly98.esdms.api;

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

import java.security.Principal;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.UserImpl;

@JsonDeserialize(as = UserImpl.class)
public interface User extends Person, Principal {

    @NotNull(message = "login is required")
    public abstract String getLogin();

    public abstract void setLogin(String login);
    
    public abstract Set<Role> getRoles();

    public abstract void setRoles(Set<Role> roles);

    public abstract String getHash();

    public abstract void setHash(String hash);

    public abstract char[] getPassword();

    public abstract void setPassword(char[] password);

    public abstract void addRole(Role role);

    public abstract void removeRole(Role role);

    public abstract boolean hasRole(Role role);
}