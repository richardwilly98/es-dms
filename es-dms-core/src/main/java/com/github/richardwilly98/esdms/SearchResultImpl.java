package com.github.richardwilly98.esdms;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.richardwilly98.esdms.api.Facet;
import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.google.common.base.Objects;

public class SearchResultImpl<T extends ItemBase> implements SearchResult<T> {

	private static Logger log = Logger.getLogger(SearchResultImpl.class);

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
			return new SearchResultImpl<T> (this);
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

	public String toString() {
		return Objects.toStringHelper(this).add("firstIndex", firstIndex)
				.add("pageSize", pageSize).add("elapsedTime", elapsedTime)
				.add("totalHits", totalHits).toString();
	}

	@Override
	public Map<String, Facet> getFacets() {
		return facets;
	}

}