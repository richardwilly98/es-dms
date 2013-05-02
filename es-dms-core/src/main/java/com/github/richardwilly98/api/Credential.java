package com.github.richardwilly98.api;


public class Credential {

	String username;
	String password;
	boolean rememberMe;

	public Credential() {
		this(null, null);
	}
	
	public Credential(String username, String password) {
		this(username, password, false);
	}

	public Credential(String username, String password, boolean rememberMe) {
		this.username = username;
		this.password = password;
		this.rememberMe = rememberMe;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

}
