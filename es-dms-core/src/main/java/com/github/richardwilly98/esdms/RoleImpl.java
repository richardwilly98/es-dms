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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.google.common.base.Objects;

public class RoleImpl extends ItemBaseImpl implements Role {

    private static final long serialVersionUID = 1L;
    private RoleType type;
    private final Set<String> scopes = newHashSet();
    private final Set<Permission> permissions = newHashSet();

    public static class Builder extends BuilderBase<Builder> {

        private RoleType type = RoleType.USER_DEFINED;
        private Set<String> scopes;
	private Set<Permission> permissions;

        public Builder type(RoleType type) {
            this.type = type;
            return getThis();
        }

        public Builder scopes(Set<String> scopes) {
	    this.scopes = scopes;
	    return getThis();
	}

	public Builder permissions(Set<Permission> permissions) {
	    this.permissions = permissions;
	    return getThis();
	}

	@Override
	protected Builder getThis() {
	    return this;
	}

	public RoleImpl build() {
	    return new RoleImpl(this);
	}
    }

    RoleImpl() {
	super(null);
    }

    protected RoleImpl(Builder builder) {
	super(builder);
	if (builder != null) {
	    this.type = builder.type;
	    if (builder.scopes != null) {
	        this.scopes.addAll(builder.scopes);
	    }
	    if (builder.permissions != null) {
	        this.permissions.addAll(builder.permissions);
	    }
	}
    }

    @Override
    public RoleType getType() {
        return type;
    }

    @Override
    public void setType(RoleType type) {
        this.type = type;
    }

    // methods on scope
    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.esdms.Role#getScopes()
     */
    @Override
    public Set<String> getScopes() {
	return scopes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.esdms.Role#setScopes(java.util.Set)
     */
    @Override
    public void setScopes(Set<String> scopes) {
	if (scopes != null) {
	    this.scopes.addAll(scopes);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.esdms.Role#addScope(java.lang.String)
     */
    @Override
    public void addScope(String scope) {
	if (!scopes.contains(scope)) {
	    scopes.add(scope);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.esdms.Role#removeScope(java.lang.String)
     */
    @Override
    public void removeScope(String scope) {
	if (scopes.contains(scope)) {
	    this.scopes.remove(scope);
	}
    }

    // end of methods on scopes

    // methods on permissions
    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.esdms.Role#getPermissions()
     */
    @Override
    public Set<Permission> getPermissions() {
	return permissions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.esdms.Role#setPermissions(java.util.Set)
     */
    @Override
    public void setPermissions(Set<Permission> permissions) {
	if (permissions != null) {
	    this.permissions.addAll(permissions);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.richardwilly98.esdms.Role#addPermission(com.github.richardwilly98
     * .esdms.PermissionImpl)
     */
    @Override
    public void addPermission(Permission permission) {
	if (permission == null) {
	    return;
	}
	if (!this.permissions.contains(permission)) {
	    permissions.add(permission);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.esdms.Role#removePermission(com.github.
     * richardwilly98.esdms.api.Permission)
     */
    @Override
    public void removePermission(Permission permission) {
        if (permission == null) {
            return;
        }
        if (permissions.contains(permission)) {
            permissions.remove(permission);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RoleImpl other = (RoleImpl) obj;
        if (permissions == null) {
            if (other.permissions != null)
                return false;
        } else if (!permissions.equals(other.permissions))
            return false;
        if (scopes == null) {
            if (other.scopes != null)
                return false;
        } else if (!scopes.equals(other.scopes))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((permissions == null) ? 0 : permissions.hashCode());
        result = prime * result + ((scopes == null) ? 0 : scopes.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", id).add("name", name).add("description", description)
                .add("attributes", getAttributes()).add("type", type).add("scopes", scopes).add("permissions", permissions).toString();
    }
}
