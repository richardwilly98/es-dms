package com.github.richardwilly98.esdms.services;

import java.util.Map;

import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.exception.ServiceException;

public interface SearchService <T extends ItemBase> {
	
	public abstract SearchResult<T> search(String criteria, int first, int pageSize) throws ServiceException;
	
	public abstract SearchResult<T> search(String criteria, int first, int pageSize, String facet) throws ServiceException;
	
	public abstract SearchResult<T> search(String criteria, int first, int pageSize, String facet, Map<String, Object> filters) throws ServiceException;
	
}