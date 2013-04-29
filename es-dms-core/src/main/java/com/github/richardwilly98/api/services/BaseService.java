package com.github.richardwilly98.api.services;

import java.util.List;
import java.util.Set;

import com.github.richardwilly98.api.exception.ServiceException;

public interface BaseService <I>{
	
	public abstract I get(String id) throws ServiceException;

	public abstract List<I> getList(String name)
			throws ServiceException;
	
	public abstract Set<I> getItems(String name)
			throws ServiceException;

	public abstract List<I> search(String criteria)
			throws ServiceException;

	public abstract I create(I item)
			throws ServiceException;

	public abstract void delete(I item)
			throws ServiceException;
	
	public abstract boolean disabled(I item)
			throws ServiceException;

	public abstract void disable(I item, boolean b)
			throws ServiceException;
}
