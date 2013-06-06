package com.github.richardwilly98.esdms.services;

import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.exception.ServiceException;

public interface SearchService <T extends SearchResult<?>> {

	public abstract T create(T item) throws ServiceException;

	public abstract void delete(T item) throws ServiceException;

	public abstract T update(T item) throws ServiceException;
	
}