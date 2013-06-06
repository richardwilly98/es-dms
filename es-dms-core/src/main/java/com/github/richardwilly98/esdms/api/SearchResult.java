package com.github.richardwilly98.esdms.api;

import java.util.Collection;
import java.util.Iterator;

public interface SearchResult <T extends Object>{
	
	public abstract void setElapsedTime(long milliseconds);		//sets the elapsed time for the search in milliseconds
	public abstract long getElapsedTime();						//returns elapsed time for the search in milliseconds
	
	public abstract int size();									//returns the size of the ResultSet
	public abstract boolean isEmpty();							//returns whether the ResultSet is empty
	public abstract boolean add(T element);						//adds the item T to the ResultSet
	public abstract boolean remove(T element);					//removes the item T from the ResultSet
	public abstract boolean	contains(T element);				//returns whether item T is contained in ResultSet
	
	public Iterator<T> iterator();
	
	public abstract boolean containsAll(Collection<T> c);		//returns whether a given collection of T items are contained in the ReturnSet
	public abstract boolean addAll(Collection<T> c);			//adds all elements of a given collection of T items to the ResultSet
	public abstract boolean removeAll(Collection<T> c);			//removes all elements of a given collection of T items from the ResultSet
	public abstract boolean retainAll(Collection<T> c);			//retains all elements of a given collection of T items in the ResultSet and removes all others
	public abstract void clear();								//removes all items from the ResultSet and resets the cursor to first item in ResultSet
	
	public abstract Object[] toArray();							//returns array of objects
	public abstract T[] toArray(T[] a);							//returns array of T items
	
}