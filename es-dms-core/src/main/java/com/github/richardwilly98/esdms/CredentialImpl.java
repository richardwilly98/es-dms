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

import java.util.Arrays;

import com.github.richardwilly98.esdms.api.Credential;
import com.google.common.base.Objects;

public class CredentialImpl implements Credential {

    private String username;
    private char[] password;
    private boolean rememberMe;

    public static class Builder {

        private String username;
        private char[] password;
        private boolean rememberMe;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(char[] password) {
            this.password = password;
            return this;
        }

        public Builder rememberMe(boolean rememberMe) {
            this.rememberMe = rememberMe;
            return this;
        }

        public Credential build() {
            return new CredentialImpl(this);
        }
    }

    CredentialImpl() {
        this(null);
    }

    protected CredentialImpl(Builder builder) {
        if (builder != null) {
            this.username = builder.username;
            this.password = builder.password;
            this.rememberMe = builder.rememberMe;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.esdms.Credential#getUsername()
     */
    @Override
    public String getUsername() {
        return username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.richardwilly98.esdms.Credential#setUsername(java.lang.String)
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.esdms.Credential#getPassword()
     */
    @Override
    public char[] getPassword() {
        return password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.richardwilly98.esdms.Credential#setPassword(java.lang.String)
     */
    @Override
    public void setPassword(char[] password) {
        this.password = password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.esdms.Credential#isRememberMe()
     */
    @Override
    public boolean isRememberMe() {
        return rememberMe;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.esdms.Credential#setRememberMe(boolean)
     */
    @Override
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(password);
        result = prime * result + (rememberMe ? 1231 : 1237);
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CredentialImpl other = (CredentialImpl) obj;
        if (!Arrays.equals(password, other.password))
            return false;
        if (rememberMe != other.rememberMe)
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("username", username).add("rememberMe", rememberMe).toString();
    }
}
