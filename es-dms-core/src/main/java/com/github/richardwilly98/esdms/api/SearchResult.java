package com.github.richardwilly98.esdms.api;

import java.util.Collection;
import java.util.Set;

public interface SearchResult <T extends Object>{
	
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