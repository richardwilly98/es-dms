package com.github.richardwilly98.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.github.richardwilly98.api.Role;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.RoleService;
import com.google.inject.Inject;

public class RoleProvider extends ProviderBase<Role> implements RoleService {

	private final static String index = "test-roles";
	private final static String type = "role";

	@Inject
	RoleProvider(Client client) {
		super(client, RoleProvider.index, RoleProvider.type, Role.class);
	}

	@Override
	protected String generateUniqueId(Role role) {
		return super.generateUniqueId(role);
	}

	protected void createIndex() {
		if (!client.admin().indices().prepareExists(index).execute()
				.actionGet().exists()) {
			client.admin().indices().prepareCreate(index).execute()
					.actionGet();
		}

	}

//	@Override
//	public Role get(String id) throws ServiceException {
//		try {
//			if (log.isTraceEnabled()) {
//				log.trace(String.format("get - %s", id));
//			}
//			GetResponse response = client.prepareGet(index, type, id)
//					.execute().actionGet();
//			if (! response.exists()) {
//				return null;
//			}
//			String json = response.getSourceAsString();
//			Role role = mapper.readValue(json, Role.class);
//			return role;
//		} catch (Throwable t) {
//			log.error("getRole failed", t);
//			throw new ServiceException(t.getLocalizedMessage());
//		}
//	}
	
	@Override
	public List<Role> getList(String name) throws ServiceException { 
		return new ArrayList<Role>(getItems(name));
	}

	@Override
	public Set<Role> getItems(String name) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("getItems - %s", name));
			}
			Set<Role> roles = new HashSet<Role>();
			SearchResponse searchResponse = client.prepareSearch(index)
					.setTypes(type).setQuery(QueryBuilders.queryString(name))
					.execute().actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			for (SearchHit hit : searchResponse.getHits().hits()) {
				String json = hit.getSourceAsString();
				try {
					Role role = mapper.readValue(json, Role.class);
					roles.add(role);
				} catch (Throwable t) {
					log.error("Json processing exception.", t);
				}
			}

			return roles;
		} catch (Throwable t) {
			log.error("getItems failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public List<Role> search(String criteria) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
