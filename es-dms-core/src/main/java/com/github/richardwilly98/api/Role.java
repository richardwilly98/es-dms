package com.github.richardwilly98.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Role extends ItemBase {

	private static final long serialVersionUID = 1L;
	final Set<String> scopes;
	final Map<String, Permission> permissions;

	public Role() {
		scopes = new HashSet<String>();
		permissions = new HashMap<String, Permission>();
	}

	// methods on scope
	public Set<String> getScopes() {
		return scopes;
	}

	public void setScopes(Set<String> scopes) {
		this.scopes.addAll(scopes);
	}

	public void addScope(String scope) {
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
	public Map<String, Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Map<String, Permission> permissions) {
		this.permissions.putAll(permissions);
	}

	public void addPermission(Permission permission) {
		if (!permissions.containsKey(permission.name)) {
			permissions.put(permission.name, permission);
		}
	}

	public void removePermission(String permission) {
		if (permissions.containsKey(permission)) {
			this.permissions.remove(permission);
		}
	}

	public void removePermission(Permission permission) {
		if (permissions.containsKey(permission.name)) {
			this.permissions.remove(permission.name);
		}
	}

}
