package com.github.richardwilly98.esdms;

import com.github.richardwilly98.esdms.api.Credential;
import com.google.common.base.Objects;

public class CredentialImpl implements Credential {

	String username;
	String password;
	boolean rememberMe;

	public CredentialImpl() {
		this(null, null);
	}

	public CredentialImpl(String username, String password) {
		this(username, password, false);
	}

	public CredentialImpl(String username, String password, boolean rememberMe) {
		this.username = username;
		this.password = password;
		this.rememberMe = rememberMe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.esdms.Credential#getUsername()
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.esdms.Credential#setUsername(java.lang.String)
	 */
	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.esdms.Credential#getPassword()
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.esdms.Credential#setPassword(java.lang.String)
	 */
	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.esdms.Credential#isRememberMe()
	 */
	@Override
	public boolean isRememberMe() {
		return rememberMe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.esdms.Credential#setRememberMe(boolean)
	 */
	@Override
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("username", username)
				.add("rememberMe", rememberMe).toString();
	}
}
