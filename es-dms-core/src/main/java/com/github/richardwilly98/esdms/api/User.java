package com.github.richardwilly98.esdms.api;

import java.security.Principal;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.UserImpl;

@JsonDeserialize(as = UserImpl.class)
public interface User extends Person, Principal {

	public abstract String getLogin();

	public abstract Set<Role> getRoles();

	public abstract void setRoles(Set<Role> roles);

	public abstract String getHash();

	public abstract void setHash(String hash);

	public abstract String getPassword();

	public abstract void setPassword(String password);

	public abstract void addRole(Role role);

	public abstract void removeRole(Role role);

	public abstract boolean hasRole(Role role);
}