package com.github.richardwilly98.esdms;

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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;

public class UserImpl extends PersonImpl implements User {

    private static final long serialVersionUID = 1L;

    private final Set<Role> roles = newHashSet();
    private String hash;
    private char[] password;
    private String login;

    public static class Builder extends PersonImpl.Builder<Builder> {

        private Set<Role> roles = newHashSet();
        private String hash;
        private char[] password;
        private String login;

        public Builder password(char[] password) {
            this.password = password;
            return getThis();
        }

        public Builder roles(Set<Role> roles) {
            this.roles = roles;
            return getThis();
        }

        public Builder hash(String hash) {
            this.hash = hash;
            return getThis();
        }

        public Builder login(String login) {
            this.login = login;
            return getThis();
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public UserImpl build() {
            UserImpl user = new UserImpl(this);
            return user;
        }
    }

    UserImpl() {
        this(null);
    }

    protected UserImpl(Builder builder) {
        super(builder);
        if (builder != null) {
            this.password = builder.password;
            if (builder.roles != null) {
                this.roles.addAll(builder.roles);
            }
            this.hash = builder.hash;
            this.login = builder.login;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.User#getLogin()
     */
    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.User#getRoles()
     */
    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.User#setRoles(java.util.Set)
     */
    @Override
    public void setRoles(Set<Role> roles) {
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.User#getHash()
     */
    @Override
    public String getHash() {
        return hash;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.User#setHash(java.lang.String)
     */
    @Override
    public void setHash(String hash) {
        this.hash = hash;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.User#getPassword()
     */
    @Override
    public char[] getPassword() {
        return password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.User#setPassword(java.lang.String)
     */
    @Override
    public void setPassword(char[] password) {
        this.password = password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.richardwilly98.api.User#addRole(com.github.richardwilly98.
     * api.Role)
     */
    @Override
    public void addRole(Role role) {
        if (role != null) {
            roles.add(role);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.richardwilly98.api.User#removeRole(com.github.richardwilly98
     * .api.Role)
     */
    @Override
    public void removeRole(Role role) {
        if (role != null)
            roles.remove(role);
    }

    @Override
    @JsonIgnore
    public boolean hasRole(Role role) {
        try {
            checkNotNull(role);
            return Iterables.contains(roles, role);
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((hash == null) ? 0 : hash.hashCode());
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        result = prime * result + Arrays.hashCode(password);
        result = prime * result + ((roles == null) ? 0 : roles.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserImpl other = (UserImpl) obj;
        if (hash == null) {
            if (other.hash != null)
                return false;
        } else if (!hash.equals(other.hash))
            return false;
        if (login == null) {
            if (other.login != null)
                return false;
        } else if (!login.equals(other.login))
            return false;
        if (!Arrays.equals(password, other.password))
            return false;
        if (roles == null) {
            if (other.roles != null)
                return false;
        } else if (!roles.equals(other.roles))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("id", getId()).add("name", getName()).add("login", login)
                .add("email", getEmail()).add("roles", getRoles()).toString();
    }

}
