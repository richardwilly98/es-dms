package com.github.richardwilly98.esdms.search.api;

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

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.search.SearchResultImpl;

@JsonDeserialize(as = SearchResultImpl.class)
public interface SearchResult<T extends ItemBase> {

    /*
     * returns elapsed time for the search in milliseconds
     */
    public abstract long getElapsedTime();

    /*
     * optional: returns the value of the current page (index div pagesize)
     */
    public abstract int getFirstIndex();

    /*
     * returns the size of the page used to transfer data from the es-dms
     * resultset collection
     */
    public abstract int getPageSize();

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

    /*
     * optional: returns the facets associated with the result set
     */
    public abstract Map<String, Facet> getFacets();

}