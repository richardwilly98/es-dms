package com.github.richardwilly98.api.services;

import java.util.List;
import java.util.Set;

import com.github.richardwilly98.api.ItemBase;
import com.github.richardwilly98.api.exception.ServiceException;

public interface BaseService<T extends ItemBase> {

	public abstract T get(String id) throws ServiceException;

	public abstract List<T> getList(String name) throws ServiceException;

	public abstract Set<T> getItems(String name) throws ServiceException;
	
	public abstract Set<T> getItems() throws ServiceException;

	public abstract List<T> search(String criteria) throws ServiceException;

	public abstract T create(T item) throws ServiceException;

	public abstract void delete(T item) throws ServiceException;

	public abstract T update(T item) throws ServiceException;

	public abstract boolean disabled(T item) throws ServiceException;

	public abstract void disable(T item, boolean b) throws ServiceException;
	
}
