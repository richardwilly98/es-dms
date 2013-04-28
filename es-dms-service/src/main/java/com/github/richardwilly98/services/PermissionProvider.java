package com.github.richardwilly98.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.PermissionService;
import com.google.inject.Inject;

public class PermissionProvider extends ProviderBase<Permission> implements PermissionService {

	private final static String index = "test-permissions";
	private final static String type = "permissions";

	@Inject
	PermissionProvider(Client client) {
		super(client, PermissionProvider.index, PermissionProvider.type);
	}

	@Override
	protected String generateUniqueId(Permission permission) {
		return super.generateUniqueId(permission);
	}

	protected void createIndex() {
		if (!client.admin().indices().prepareExists(index).execute()
				.actionGet().exists()) {
			client.admin().indices().prepareCreate(index).execute()
					.actionGet();
			// Force index to be refreshed.
			client.admin().indices().refresh(new RefreshRequest(index))
					.actionGet();
		}

	}

	@Override
	public Permission get(String id) throws ServiceException {
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
			Permission permission = mapper.readValue(json, Permission.class);
			return permission;
		} catch (Throwable t) {
			log.error("getPermission failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}
	
	@Override
	public List<Permission> getList(String name) throws ServiceException { 
		return new ArrayList<Permission>(getItems(name));
	}

	@Override
	public Set<Permission> getItems(String name) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("getItems - %s", name));
			}
			Set<Permission> permissions = new HashSet<Permission>();
			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type).setQuery(QueryBuilders.queryString(name))
					.execute().actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			for (SearchHit hit : searchResponse.getHits().hits()) {
				String json = hit.getSourceAsString();
				try {
					Permission permission = mapper.readValue(json, Permission.class);
					permissions.add(permission);
				} catch (Throwable t) {
					log.error("Json processing exception.", t);
				}
			}

			return permissions;
		} catch (Throwable t) {
			log.error("getItems failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public List<Permission> search(String criteria) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
