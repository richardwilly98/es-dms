package com.github.richardwilly98.esdms.services;

import java.util.Set;

import com.github.richardwilly98.esdms.SessionImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.exception.ServiceException;

public interface AuthenticationService extends BaseService<SessionImpl> {
	
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

	public abstract SessionImpl get(String id) throws ServiceException;

	public abstract Set<SessionImpl> getItems(String name) throws ServiceException;
	
	public abstract SessionImpl create(SessionImpl item) throws ServiceException;

	public abstract void delete(SessionImpl item) throws ServiceException;

	public abstract SessionImpl update(SessionImpl item) throws ServiceException;

}