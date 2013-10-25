package com.github.richardwilly98.esdms.search;

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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.richardwilly98.esdms.search.api.Facet;
import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.search.api.SearchResult;
import com.google.common.base.Objects;

public class SearchResultImpl<T extends ItemBase> implements SearchResult<T> {

    private final Set<T> items = newHashSet();
    private long elapsedTime;
    private int firstIndex;
    private int pageSize;
    private long totalHits;
    private final Map<String, Facet> facets = newHashMap();

    public static class Builder<T extends ItemBase> {

	private long elapsedTime;
	private final Set<T> items = newHashSet();
	private int firstIndex;
	private int pageSize;
	private long totalHits;
	private final Map<String, Facet> facets = newHashMap();

	public Builder<T> elapsedTime(long elapsedTime) {
	    this.elapsedTime = elapsedTime;
	    return this;
	}

	public Builder<T> items(Set<T> items) {
	    checkNotNull(items);
	    this.items.addAll(items);
	    return this;
	}

	public Builder<T> firstIndex(int firstIndex) {
	    this.firstIndex = firstIndex;
	    return this;
	}

	public Builder<T> pageSize(int pageSize) {
	    this.pageSize = pageSize;
	    return this;
	}

	public Builder<T> totalHits(long totalHits) {
	    this.totalHits = totalHits;
	    return this;
	}

	public Builder<T> facets(Map<String, Facet> facets) {
	    checkNotNull(facets);
	    this.facets.putAll(facets);
	    return this;
	}

	public SearchResultImpl<T> build() {
	    return new SearchResultImpl<T>(this);
	}
    }

    SearchResultImpl() {
	this(null);
    }

    public SearchResultImpl(Builder<T> builder) {
	if (builder != null) {
	    this.firstIndex = builder.firstIndex;
	    this.elapsedTime = builder.elapsedTime;
	    this.pageSize = builder.pageSize;
	    this.totalHits = builder.totalHits;
	    this.items.addAll(builder.items);
	    this.facets.putAll(builder.facets);
	}
    }

    @Override
    public int getFirstIndex() {
	return firstIndex;
    }

    @Override
    public int getPageSize() {
	return pageSize;
    }

    @Override
    public long getTotalHits() {
	return totalHits;
    }

    @Override
    public Set<T> getItems() {
	return this.items;
    }

    @Override
    public long getElapsedTime() {
	return this.elapsedTime;
    }

    @JsonIgnore
    @Override
    public Set<String> getColumns() {
	throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Facet> getFacets() {
        return facets;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (elapsedTime ^ (elapsedTime >>> 32));
        result = prime * result + ((facets == null) ? 0 : facets.hashCode());
        result = prime * result + firstIndex;
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        result = prime * result + pageSize;
        result = prime * result + (int) (totalHits ^ (totalHits >>> 32));
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SearchResultImpl<T> other = (SearchResultImpl<T>) obj;
        if (elapsedTime != other.elapsedTime)
            return false;
        if (facets == null) {
            if (other.facets != null)
                return false;
        } else if (!facets.equals(other.facets))
            return false;
        if (firstIndex != other.firstIndex)
            return false;
        if (items == null) {
            if (other.items != null)
                return false;
        } else if (!items.equals(other.items))
            return false;
        if (pageSize != other.pageSize)
            return false;
        if (totalHits != other.totalHits)
            return false;
        return true;
    }

    @Override
    public String toString() {
	return Objects.toStringHelper(this).add("firstIndex", firstIndex).add("pageSize", pageSize).add("elapsedTime", elapsedTime)
	        .add("totalHits", totalHits).toString();
    }

}