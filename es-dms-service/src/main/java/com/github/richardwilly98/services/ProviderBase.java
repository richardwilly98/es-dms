package com.github.richardwilly98.services;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.api.ItemBase;
import com.github.richardwilly98.api.Settings;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.BaseService;
import com.github.richardwilly98.api.services.BootstrapService;

abstract class ProviderBase<T extends ItemBase> implements BaseService<T> {

	final protected Logger log = Logger.getLogger(getClass());

	final static ObjectMapper mapper = new ObjectMapper();
	final Client client;
	final Settings settings;
	final String index;
	final String type;
	final Class<T> clazz;

	@Inject
	ProviderBase(final Client client, final BootstrapService bootstrapService,
			final String index, final String type, final Class<T> clazz) throws ServiceException {
		checkNotNull(client);
		checkNotNull(bootstrapService);
		checkNotNull(type);
		checkNotNull(clazz);
		this.client = client;
		this.settings = bootstrapService.loadSettings();
		if (index != null) {
			this.index = index;
		} else {
			this.index = settings.getLibrary();
		}
		this.type = type;
		this.clazz = clazz;
	}

	@Override
	public T get(String id) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("get - %s", id));
			}
			GetResponse response = client.prepareGet(index, type, id).execute()
					.actionGet();
			if (!response.isExists()) {
				return null;
			}
			String json = response.getSourceAsString();
			T item = mapper.readValue(json, clazz);
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
				log.trace(String.format("getItems - %s", name));
			}
			Set<T> items = new HashSet<T>();
			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type).setQuery(QueryBuilders.queryString(name))
					.execute().actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			for (SearchHit hit : searchResponse.getHits().hits()) {
				String json = hit.getSourceAsString();
				try {
					T item = mapper.readValue(json, clazz);
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
	public Set<T> getItems() throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("get all items in List"));
			}
			Set<T> items = new HashSet<T>();

			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type).setQuery(QueryBuilders.queryString("*"))
					.execute().actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			for (SearchHit hit : searchResponse.getHits().hits()) {
				String json = hit.getSourceAsString();
				try {
					T item = mapper.readValue(json, clazz);
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
					.setTypes(type).setSearchType(SearchType.QUERY_AND_FETCH)
					.setQuery(new QueryStringQueryBuilder(criteria)).execute()
					.actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			for (SearchHit hit : searchResponse.getHits().hits()) {
				String json = hit.getSourceAsString();
				T item = mapper.readValue(json, clazz);
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
	public T update(T item) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("create - %s", item));
			}
			String json;
			json = mapper.writeValueAsString(item);
			UpdateResponse response = client
					.prepareUpdate(index, type, item.getId()).setDoc(json)
					.execute().actionGet();
			refreshIndex();
			T updatedItem = get(response.getId());
			return updatedItem;
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
		return false;
	}

	@Override
	public void disable(T item, boolean b) throws ServiceException {
	}

	protected String generateUniqueId(T item) {
		return UUID.randomUUID().toString();
	}

	@PostConstruct
	private void createIndex() throws ServiceException {
		if (!client.admin().indices().prepareExists(index).execute()
				.actionGet().isExists()) {
			client.admin().indices().prepareCreate(index).execute().actionGet();
			refreshIndex();
		}
		log.info("Exists type "
				+ type
				+ " in index "
				+ index
				+ ": "
				+ client.admin().indices().prepareTypesExists(index)
						.setTypes(type).execute().actionGet().isExists());
		if (!client.admin().indices().prepareTypesExists(index).setTypes(type)
				.execute().actionGet().isExists()) {
			String mapping = getMapping();
			if (mapping != null) {
				log.debug(String.format("Create mapping %s", mapping));
				PutMappingResponse mappingResponse = client.admin().indices()
						.preparePutMapping(index).setType(type)
						.setSource(mapping).execute().actionGet();
				log.info(String.format("Mapping response acknowledged: %s",
						mappingResponse.isAcknowledged()));
			}
			loadInitialData();
			refreshIndex();
		}
	}

	protected void refreshIndex() {
		client.admin().indices().refresh(new RefreshRequest(index)).actionGet();
	}

	protected abstract void loadInitialData() throws ServiceException;

	protected abstract String getMapping();
}
