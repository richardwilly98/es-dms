package com.github.richardwilly98.esdms.api;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.RoleImpl;

@JsonDeserialize(as = RoleImpl.class)
public interface Role extends ItemBase {

	// methods on scope
	public abstract Set<String> getScopes();

	public abstract void setScopes(Set<String> scopes);

	public abstract void addScope(String scope);

	public abstract void removeScope(String scope);

	// methods on permissions
	public abstract Set<Permission> getPermissions();

	public abstract void setPermissions(Set<Permission> permissions);

	public abstract void addPermission(Permission permission);

	public abstract void removePermission(Permission permission);

}