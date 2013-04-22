package com.github.richardwilly98.api.services;

import java.util.List;

import com.github.richardwilly98.api.Document;
import com.github.richardwilly98.api.exception.ServiceException;

public interface DocumentService {

	public abstract Document getDocument(String id) throws ServiceException;

	public abstract List<Document> getDocuments(String name)
			throws ServiceException;

	public abstract List<Document> contentSearch(String criteria)
			throws ServiceException;

	public abstract String createDocument(Document document)
			throws ServiceException;

	public abstract void deleteDocument(Document document)
			throws ServiceException;
	
	public abstract void checkinDocument(Document document)
			throws ServiceException;

	public abstract void checkoutDocument(Document document)
			throws ServiceException;
}