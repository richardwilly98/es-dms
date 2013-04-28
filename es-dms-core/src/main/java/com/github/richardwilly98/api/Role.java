package com.github.richardwilly98.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Role extends ItemBase{
	
	Set <String> scopes;
	Map <String, Permission> permissions;

	//methods on scope
	public Set <String> getScopes() {
		return scopes;
	}

	public void setScopes(Set <String> scopes) {
		this.scopes = scopes;
	}
	
	public void addScope(String scope) {
		if(scopes == null) scopes = new HashSet<String>();
		if (!scopes.contains(scope)) scopes.add(scope);
	}

	public void removeScope(String scope) {
		if(scopes == null) return;
		if (scopes.contains(scope)) this.scopes.remove(scope);
	}
	//end of methods on scopes
	
	//methods on permissions
	public Map <String, Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Map <String, Permission> permissions) {
		this.permissions = permissions;
	}
	
	public void addPermission(Permission permission) {
		if(permissions == null) permissions = new HashMap<String, Permission>();
		if (!permissions.containsKey(permission.name)) permissions.put(permission.name, permission);
	}

	public void removePermission(String permission) {
		if(permissions == null) return;
		if (permissions.containsKey(permission)) this.permissions.remove(permission);
	}
	
	public void removePermission(Permission permission) {
		if(permissions == null) return;
		if (permissions.containsKey(permission.name)) this.permissions.remove(permission.name);
	}

}
