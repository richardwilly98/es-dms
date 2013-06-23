package com.github.richardwilly98.esdms.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.SearchResultImpl;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.ItemBase;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.Settings;
import com.github.richardwilly98.esdms.exception.ServiceException;

abstract class ProviderBase<T extends ItemBase> implements BaseService<T> {

	final protected Logger log = Logger.getLogger(getClass());

	final static ObjectMapper mapper = new ObjectMapper();
	final Client client;
	final Settings settings;
	final String index;
	final String type;
	final Class<T> clazz;

	private String currentUser;

	@Inject
	ProviderBase(final Client client, final BootstrapService bootstrapService,
			final String index, final String type, final Class<T> clazz)
			throws ServiceException {
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

	protected void isAuthenticated() throws ServiceException {
		try {
			log.debug("*** isAuthenticated ***");
			Subject currentSubject = SecurityUtils.getSubject();
			log.debug("currentSubject.isAuthenticated(): "
					+ currentSubject.isAuthenticated());
			log.debug("Principal: " + currentSubject.getPrincipal());
			if (currentSubject.getPrincipal() == null) {
				throw new ServiceException("Unauthorize request");
			} else {
				if (currentUser == null) {
					if (currentSubject.getPrincipal() instanceof UserImpl) {
						currentUser = ((UserImpl) currentSubject.getPrincipal())
								.getId();
					}
				}
			}
		} catch (Throwable t) {
			throw new ServiceException();
		}
	}

	protected String getCurrentUser() throws ServiceException {
		if (currentUser == null) {
			isAuthenticated();
		}
		return currentUser;
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
				log.info(String.format("Cannot find item %s", id));
				return null;
			}
			String json = response.getSourceAsString();
			T item = mapper.readValue(json, clazz);
			return item;
		} catch (Throwable t) {
			log.error("get failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public SearchResult<T> search(String criteria, int first, int pageSize)
			throws ServiceException {
		try {
//			Set<T> items = newHashSet();

			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type)
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setFrom(first).setSize(pageSize)
					.setQuery(new QueryStringQueryBuilder(criteria)).execute()
					.actionGet();
//			long totalHits = searchResponse.getHits().totalHits();
//			long elapsedTime = searchResponse.getTookInMillis();
//			log.debug("totalHits: " + totalHits);
//			for (SearchHit hit : searchResponse.getHits().hits()) {
//				String json = hit.getSourceAsString();
//				T item = mapper.readValue(json, clazz);
//				items.add(item);
//			}
//
//			SearchResult<T> searchResult = new SearchResultImpl.Builder<T>()
//					.totalHits(totalHits).elapsedTime(elapsedTime).items(items)
//					.firstIndex(first).pageSize(pageSize).build();
//			return searchResult;
			return getSearchResult(searchResponse, first, pageSize);
		} catch (Throwable t) {
			log.error("search failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	protected SearchResult<T> getSearchResult(SearchResponse searchResponse,
			int first, int pageSize) throws ServiceException {
		log.trace("** getSearchResult **");
		try {
			Set<T> items = newHashSet();
			long totalHits = searchResponse.getHits().totalHits();
			long elapsedTime = searchResponse.getTookInMillis();
			for (SearchHit hit : searchResponse.getHits().hits()) {
				String json = hit.getSourceAsString();
				T item = mapper.readValue(json, clazz);
				items.add(item);
			}
			SearchResult<T> searchResult = new SearchResultImpl.Builder<T>()
					.totalHits(totalHits).elapsedTime(elapsedTime).items(items)
					.firstIndex(first).pageSize(pageSize).build();
			return searchResult;
		} catch (Throwable t) {
			log.error("getSearchResult failed", t);
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

	// TODO: Must improve the update to avoid 2 update API calls
	@Override
	public T update(T item) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("update - %s", item));
			}
			String json;
			json = mapper.writeValueAsString(item);
			UpdateResponse response = client
					.prepareUpdate(index, type, item.getId())
					.setScript("ctx._source.remove('attributes');").execute()
					.actionGet();
			response = client.prepareUpdate(index, type, item.getId())
					.setDoc(json).execute().actionGet();
			refreshIndex();
			T updatedItem = get(response.getId());
			return updatedItem;
		} catch (Throwable t) {
			log.error("update failed", t);
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
		boolean exists = client.admin().indices().prepareTypesExists(index)
				.setTypes(type).execute().actionGet().isExists();
		log.info(String.format("Exists type %s in index %s: %s", type, index,
				exists));
		if (!exists) {
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

	@Override
	public boolean disabled(T item) throws ServiceException {
		try {
			checkNotNull(item);
			return item.isDisabled();
		} catch (Throwable t) {
			log.error("disabled failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public void disable(T item, boolean b) throws ServiceException {
		try {
			checkNotNull(item);
			item.setDisabled(b);
		} catch (Throwable t) {
			log.error("disable failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	protected void refreshIndex() {
		if (settings.isIndexRefresh()) {
			client.admin().indices().refresh(new RefreshRequest(index))
					.actionGet();
		}
	}

	protected abstract void loadInitialData() throws ServiceException;

	protected abstract String getMapping();
}
