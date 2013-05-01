package com.github.richardwilly98.api.services;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.ISession;
import com.github.richardwilly98.api.exception.ServiceException;

public interface AuthenticationService extends BaseService <ISession> {
	
	public abstract String login(Credential credential)
			throws ServiceException;

	public abstract void logout(String token)
			throws ServiceException;
	
	public abstract void validate(String token)
			throws ServiceException;
	
	public boolean hasRole(String token, String role)
			throws ServiceException;

	public boolean hasPermission(String token, String permission)
			throws ServiceException;
}