package com.github.richardwilly98.esdms.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.CredentialImpl;

@JsonDeserialize(as = CredentialImpl.class)
public interface Credential {

	public abstract String getUsername();

	public abstract void setUsername(String username);

	public abstract String getPassword();

	public abstract void setPassword(String password);

	public abstract boolean isRememberMe();

	public abstract void setRememberMe(boolean rememberMe);

}