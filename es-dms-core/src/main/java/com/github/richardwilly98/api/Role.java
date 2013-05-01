package com.github.richardwilly98.api;

import java.util.HashSet;
import java.util.Set;

public class Role extends ItemBase {

	private static final long serialVersionUID = 1L;
	Set<String> scopes;
	Set<Permission> permissions;

	public Role() {
	}

	// methods on scope
	public Set<String> getScopes() {
		return scopes;
	}

	public void setScopes(Set<String> scopes) {
		if (scopes != null) {
			if (this.scopes == null) {
				this.scopes = new HashSet<String>();
			}
			this.scopes.addAll(scopes);
		}
	}

	public void addScope(String scope) {
		if (scopes == null) {
			scopes = new HashSet<String>();
		}
		if (!scopes.contains(scope)) {
			scopes.add(scope);
		}
	}

	public void removeScope(String scope) {
		if (scopes.contains(scope)) {
			this.scopes.remove(scope);
		}
	}

	// end of methods on scopes

	// methods on permissions
	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		if (permissions != null) {
			if (this.permissions == null) {
				this.permissions = new HashSet<Permission>();
			}
			this.permissions.addAll(permissions);
		}
	}

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

	public void removePermission(Permission permission) {
		if (permission == null) {
			return;
		}
		if (permissions.contains(permission)) {
			permissions.remove(permission);
		}
	}

}
