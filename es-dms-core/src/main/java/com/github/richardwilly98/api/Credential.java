package com.github.richardwilly98.api;


public class Credential {

	String username;
	String password;

	public Credential() {
		this(null, null);
	}
	
	public Credential(String username, String password) {
		this.username = username;
		this.password = password;
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

}
