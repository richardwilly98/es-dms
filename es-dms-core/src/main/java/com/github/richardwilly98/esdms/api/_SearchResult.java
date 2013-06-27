package com.github.richardwilly98.esdms.api;

/*
 * #%L
 * es-dms-core
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.util.Collection;
import java.util.Set;

public interface _SearchResult <T extends Object>{
	
	public abstract void setElapsedTime(long milliseconds);		//sets the elapsed time for the search in milliseconds
	public abstract long getElapsedTime();						//returns elapsed time for the search in milliseconds
	
	public abstract void first(); 								//resets index position to first element of result set (element 0)
	
	public abstract void setPageIndex(int page);				//optional: moves index to page position (page * pagesize)
	public abstract int	getPageIndex();							//optional: returns the value of the current page (index div pagesize)

	
	public abstract int	getPageSize();							//returns the size of the page used to transfer data from the es-dms resultset collection
	public abstract void setPageSize(int size);					//sets the size of the page used to transfer data from the es-dms resultset collection
	
	public abstract String	getStatement();						//optional: return the search criteria used for this result set
	public abstract void setStatement(String statement);		//optional: sets the search criteria

	public abstract Set<String> getColumns();					//optional: returns the list of columns present in the search criteria
	public abstract void setView(Set<String> view);				//optional: defines a set of columns to be used as a view (only the view is returned from the resultSet)
	public abstract Set<String> getView();						//optional: returns the current view
	
	public abstract int getTotalHits();							//returns the total size of the collection in the ResultSet
	public abstract boolean	hasData();							//optional: returns whether there is still data at the current index location
	public abstract Set<T> getItems();							//optional: returns the complete result set

	public abstract Set<T>	getNextPage();						//returns pagesize of result items starting from index position, moving index to start-index-position + pagesize
	public abstract Set<T>	getPage(int page);					//returns pagesize of result items (if possible) starting from page * page size position

	public abstract boolean add(T element);						//adds the item T to the ResultSet
	public abstract boolean remove(T element);					//removes the item T from the ResultSet
	public abstract boolean	contains(T element);				//returns whether item T is contained in ResultSet
	
	public abstract boolean containsAll(Collection<T> c);		//returns whether a given collection of T items are contained in the ReturnSet
	public abstract boolean addAll(Collection<T> c);			//adds all elements of a given collection of T items to the ResultSet
	public abstract boolean removeAll(Collection<T> c);			//removes all elements of a given collection of T items from the ResultSet
	public abstract boolean retainAll(Collection<T> c);			//retains all elements of a given collection of T items in the ResultSet and removes all others
	public abstract void clear();								//removes all items from the ResultSet and resets the cursor to first item in ResultSet
	
	public abstract Object[] toArray();							//returns array of objects
	public abstract T[] toArray(T[] a);							//returns array of T items
	
}