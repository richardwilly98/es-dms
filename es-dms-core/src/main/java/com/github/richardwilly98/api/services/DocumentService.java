package com.github.richardwilly98.api.services;

import java.util.List;

import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.api.exception.ServiceException;

public interface DocumentService {

	public abstract Document get(String id) throws ServiceException;

	public abstract List<Document> getDocuments(String name)
			throws ServiceException;

	public abstract List<Document> search(String criteria)
			throws ServiceException;

	public abstract String create(Document document)
			throws ServiceException;

	public abstract void delete(Document document)
			throws ServiceException;
	
	public abstract void checkin(Document document)
			throws ServiceException;

	public abstract void checkout(Document document)
			throws ServiceException;
}