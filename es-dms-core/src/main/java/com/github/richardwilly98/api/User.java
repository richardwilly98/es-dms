package com.github.richardwilly98.api;

import java.security.Principal;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class User extends Person implements Principal {
	
	private static final long serialVersionUID = 1L;
	Set<Role> roles;
	String hash;
	
	@JsonIgnore
	String password;

	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@JsonIgnore
	public String getLogin() {
		return email;
	}
	@Override
	public String toString() {
		return getId() + " - " + getLogin();
	}
}
