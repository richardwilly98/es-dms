package com.github.richardwilly98.api;


public class Credential {

	String login;
	String password;

	public Credential() {
		this(null, null);
	}
	
	public Credential(String login, String password) {
		this.login = login;
		this.password = password;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
