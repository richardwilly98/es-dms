package com.github.richardwilly98.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.github.richardwilly98.User;

public class UserProvider extends ProviderBase {

	private static Logger log = Logger.getLogger(UserProvider.class);
	private final static String index = "test-users";
	private final static String type = "user";

	private List<User> users = Arrays.asList(new User("dsandron", "Danilo",
			"Bankok"), new User("rlouapre", "Richard", "Jersey City"));

	public User getUser(String id) throws ServiceException {
		try {
			GetResponse response = getClient().prepareGet(index, type, id)
					.execute().actionGet();
			String json = response.getSourceAsString();
			User user = mapper.readValue(json, User.class);
			return user;
		} catch (Throwable t) {
			log.error("getUser failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	public List<User> getUsers(String name) throws ServiceException {
		try {
			List<User> users = new ArrayList<User>();
			if (!getClient().admin().indices().prepareExists(index).execute()
					.actionGet().exists()) {
				getClient().admin().indices().prepareCreate(index).execute()
						.actionGet();
				// Force index to be refreshed.
				getClient().admin().indices()
						.refresh(new RefreshRequest(index)).actionGet();
				for (User u : this.users) {
					String json;
					json = mapper.writeValueAsString(u);
					IndexResponse response = getClient()
							.prepareIndex(index, type).setId(u.getId())
							.setSource(json).execute().actionGet();
					log.trace(String.format("Index: %s  - Type: %s - Id: %s",
							response.getIndex(), response.getType(),
							response.getId()));
				}
			}
			SearchResponse searchResponse = getClient().prepareSearch(index)
					.setTypes(type).setQuery(QueryBuilders.queryString(name))
					.execute().actionGet();
			log.debug("totalHits: " + searchResponse.getHits().totalHits());
			for (SearchHit hit : searchResponse.getHits().hits()) {
				String json = hit.getSourceAsString();
				try {
					User user = mapper.readValue(json, User.class);
					users.add(user);
				} catch (Throwable t) {
					log.error("Json processing exception.", t);
				}
			}

			return users;
		} catch (Throwable t) {
			log.error("getUser failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	public String createUser(User user) throws ServiceException {
		try {
			if (user.getId() == null) {
				user.setId(generateUniqueId(user));
			}
			String json;
			json = mapper.writeValueAsString(user);
			log.trace(json);
			IndexResponse response = getClient().prepareIndex(index, type)
					.setId(user.getId()).setSource(json).execute().actionGet();
			log.trace(String.format("Index: %s  - Type: %s - Id: %s",
					response.getIndex(), response.getType(), response.getId()));
			return response.getId();
		} catch (Throwable t) {
			log.error("getUser failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	private String generateUniqueId(User user) {
		return super.generateUniqueId();
	}

	protected void createIndex() throws IOException {
		if (!getClient().admin().indices().prepareExists(index).execute()
				.actionGet().exists()) {
			getClient().admin().indices().prepareCreate(index).execute()
					.actionGet();
			// Force index to be refreshed.
			getClient().admin().indices().refresh(new RefreshRequest(index))
					.actionGet();
		}

	}
}
