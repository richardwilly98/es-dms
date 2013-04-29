package com.github.richardwilly98.api.services;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.exception.ServiceException;

public interface AuthenticationService {
	
	public abstract String login(Credential credential)
			throws ServiceException;

	public abstract void logout(String token)
			throws ServiceException;
}