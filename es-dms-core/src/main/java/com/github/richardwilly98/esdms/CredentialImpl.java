package com.github.richardwilly98.esdms;

import com.github.richardwilly98.esdms.api.Credential;
import com.google.common.base.Objects;

public class CredentialImpl implements Credential {

	private String username;
	private String password;
	private boolean rememberMe;

	public static class Builder {

		private String username;
		private String password;
		private boolean rememberMe;

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder rememberMe(boolean rememberMe) {
			this.rememberMe = rememberMe;
			return this;
		}

		public Credential build() {
			return new CredentialImpl(this);
		}
	}

	CredentialImpl() {
		this(null);
	}
	private CredentialImpl(Builder builder) {
		if (builder != null) {
			this.username = builder.username;
			this.password = builder.password;
			this.rememberMe = builder.rememberMe;
		}
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
