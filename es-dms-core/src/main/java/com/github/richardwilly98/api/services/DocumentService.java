package com.github.richardwilly98.api.services;

import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.api.exception.ServiceException;

public interface DocumentService extends BaseService <Document> {
	
	public static final String CREATE_PERMISSION = "document:create";
	public static final String DELETE_PERMISSION = "document:delete";
	
	public abstract void checkin(Document document)
			throws ServiceException;

	public abstract void checkout(Document document)
			throws ServiceException;
}