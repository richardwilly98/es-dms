package com.github.richardwilly98.api.services;

import java.util.List;

import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.api.exception.ServiceException;

public interface DocumentService extends BaseService <Document>{
	
	public abstract List<Document> getDocuments(String name)
			throws ServiceException;
	
	public abstract void checkin(Document document)
			throws ServiceException;

	public abstract void checkout(Document document)
			throws ServiceException;
}