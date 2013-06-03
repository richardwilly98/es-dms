package com.github.richardwilly98.esdms;

import java.util.HashSet;
import java.util.Set;

import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;

public class RoleImpl extends ItemBaseImpl implements Role {

	private static final long serialVersionUID = 1L;
	Set<String> scopes;
	Set<Permission> permissions;

	public RoleImpl() {
	}

	// methods on scope
	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Role#getScopes()
	 */
	@Override
	public Set<String> getScopes() {
		return scopes;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Role#setScopes(java.util.Set)
	 */
	@Override
	public void setScopes(Set<String> scopes) {
		if (scopes != null) {
			if (this.scopes == null) {
				this.scopes = new HashSet<String>();
			}
			this.scopes.addAll(scopes);
		}
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Role#addScope(java.lang.String)
	 */
	@Override
	public void addScope(String scope) {
		if (scopes == null) {
			scopes = new HashSet<String>();
		}
		if (!scopes.contains(scope)) {
			scopes.add(scope);
		}
	}

	/* (non-Javadoc)
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
	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Role#getPermissions()
	 */
	@Override
	public Set<Permission> getPermissions() {
		return permissions;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Role#setPermissions(java.util.Set)
	 */
	@Override
	public void setPermissions(Set<Permission> permissions) {
		if (permissions != null) {
			if (this.permissions == null) {
				this.permissions = new HashSet<Permission>();
			}
			this.permissions.addAll(permissions);
		}
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Role#addPermission(com.github.richardwilly98.esdms.PermissionImpl)
	 */
	@Override
	public void addPermission(Permission permission) {
		if (permission == null) {
			return;
		}
		if (this.permissions == null) {
			this.permissions = new HashSet<Permission>();
		}
		if (!this.permissions.contains(permission)) {
			permissions.add(permission);
		}
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Role#removePermission(com.github.richardwilly98.esdms.api.Permission)
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

}
