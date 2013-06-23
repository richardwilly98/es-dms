package com.github.richardwilly98.esdms.api;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.SearchResultImpl;

@JsonDeserialize(as = SearchResultImpl.class)
public interface SearchResult <T extends ItemBase>{

	/*
	 * returns elapsed time for the search in milliseconds
	 */
	public abstract long getElapsedTime();
	
	/*
	 * optional: returns the value of the current page (index div pagesize) 
	 */
	public abstract int	getFirstIndex();						
	
	/*
	 * returns the size of the page used to transfer data from the es-dms resultset collection
	 */
	public abstract int	getPageSize();
	
	/*
	 * optional: returns the list of columns present in the search criteria
	 */
	public abstract Set<String> getColumns();
	
	/*
	 * returns the total size of the collection in the ResultSet
	 */
	public abstract long getTotalHits();

	/*
	 * optional: returns the complete result set
	 */
	public abstract Set<T> getItems();

}