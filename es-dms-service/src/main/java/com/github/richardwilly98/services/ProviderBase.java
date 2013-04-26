package com.github.richardwilly98.services;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

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

	@Inject
	ProviderBase(Client client, String index, String type) {
		this.client = client;
		this.index = index;
		this.type = type;
		createIndex();
		refreshIndex();
	}

	// @Override
	// public T get(String id) throws ServiceException {
	// // TODO Auto-generated method stub
	// return null;
	// }

	// @Override
	// public List<T> getList(String name) throws ServiceException {
	// try {
	// List<T> users = new ArrayList<T>();
	// SearchResponse searchResponse = client.prepareSearch(index)
	// .setTypes(type).setQuery(QueryBuilders.queryString(name))
	// .execute().actionGet();
	// log.debug("totalHits: " + searchResponse.getHits().totalHits());
	// for (SearchHit hit : searchResponse.getHits().hits()) {
	// String json = hit.getSourceAsString();
	// try {
	// T user = mapper.readValue(json, typeClass);
	// users.add(user);
	// } catch (Throwable t) {
	// log.error("Json processing exception.", t);
	// }
	// }
	//
	// return users;
	// } catch (Throwable t) {
	// log.error("getUser failed", t);
	// throw new ServiceException(t.getLocalizedMessage());
	// }
	// }

	@Override
	public String create(T item) throws ServiceException {
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
			return response.getId();
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
