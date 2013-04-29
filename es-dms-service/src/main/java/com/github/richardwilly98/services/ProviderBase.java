package com.github.richardwilly98.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.api.ItemBase;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.BaseService;

abstract class ProviderBase<T extends ItemBase> implements BaseService<T> {

	Logger log = Logger.getLogger(this.getClass());

	final static ObjectMapper mapper = new ObjectMapper();
	final Client client;

	final String index;
	final String type;
	final Class<T> typeParameterClass;

	@Inject
	ProviderBase(Client client, String index, String type, Class<T> typeParameterClass) {
		this.client = client;
		this.index = index;
		this.type = type;
		this.typeParameterClass = typeParameterClass;
		createIndex();
		refreshIndex();
	}

	@Override
	public T get(String id) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("get - %s", id));
			}
			GetResponse response = client.prepareGet(index, type, id)
					.execute().actionGet();
			if (! response.exists()) {
				return null;
			}
			String json = response.getSourceAsString();
			T item = mapper.readValue(json, typeParameterClass);
			return item;
		} catch (Throwable t) {
			log.error("getUser failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public List<T> getList(String name) throws ServiceException {
		return new ArrayList<T>(getItems(name));
	}

	@Override
	public Set<T> getItems(String name) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("getList - %s", name));
			}
			Set<T> items = new HashSet<T>();
			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type).setQuery(QueryBuilders.queryString(name))
					.execute().actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			for (SearchHit hit : searchResponse.getHits().hits()) {
				String json = hit.getSourceAsString();
				try {
					T item = mapper.readValue(json, typeParameterClass);
					items.add(item);
				} catch (Throwable t) {
					log.error("Json processing exception.", t);
				}
			}

			return items;
		} catch (Throwable t) {
			log.error("getItems failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public List<T> search(String criteria) throws ServiceException {
		try {
			List<T> items = new ArrayList<T>();

			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type).setSearchType(SearchType.QUERY_AND_FETCH).setQuery(new QueryStringQueryBuilder(criteria))
					.execute().actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			for (SearchHit hit : searchResponse.getHits().hits()) {
				String json = hit.getSourceAsString();
				T item = mapper.readValue(json, typeParameterClass);
				items.add(item);
			}

			return items;
		} catch (Throwable t) {
			log.error("search failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public T create(T item) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("create - %s", item));
			}
			if (item.getId() == null) {
				item.setId(generateUniqueId(item));
			}
			String json;
			json = mapper.writeValueAsString(item);
			IndexResponse response = client.prepareIndex(index, type)
					.setId(item.getId()).setSource(json).execute().actionGet();
			log.trace(String.format("Index: %s - Type: %s - Id: %s",
					response.getIndex(), response.getType(), response.getId()));
			refreshIndex();
			T newItem = get(response.getId());
			return newItem;
		} catch (Throwable t) {
			log.error("create failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public void delete(T item) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("delete - %s", item));
			}
			if (item == null) {
				throw new IllegalArgumentException("item is null");
			}
			client.prepareDelete(index, type, item.getId()).execute()
					.actionGet();
			refreshIndex();
		} catch (Throwable t) {
			log.error("delete failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public boolean disabled(T item) throws ServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void disable(T item, boolean b) throws ServiceException {
		// TODO Auto-generated method stub

	}

	protected String generateUniqueId(T item) {
		return UUID.randomUUID().toString();
	}

	protected abstract void createIndex();

	/*
	 * Force index to be refreshed.
	 */
	protected void refreshIndex() {
		client.admin().indices().refresh(new RefreshRequest(index)).actionGet();
	}

}
