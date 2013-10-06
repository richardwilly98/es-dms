package com.github.richardwilly98.esdms.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.google.common.collect.Iterables;

public class RestUserServiceClient extends RestClientBase {

	public static final String SEARCH_PATH = "search";
	public static final String USERS_PATH = "users";

	public RestUserServiceClient(String url) {
		super(url);
	}

	public Collection<User> findUsersById(String token, String id) throws ServiceException {
		checkNotNull(token);
		checkNotNull(id);
		Collection<User> users = find(token, "id:" + id);
		return users;
	}

	public User findUserById(String token, String id) throws ServiceException {
		checkNotNull(token);
		checkNotNull(id);
		Cookie cookie = newUserCookie(token);
		Response response = getWebTarget().path(USERS_PATH)
				.path(id).request(MediaType.APPLICATION_JSON)
				.cookie(cookie).get();
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return response.readEntity(User.class);
		}
		return null;
	}

	public Collection<User> findUsersByEmail(String token, String email) throws ServiceException {
		checkNotNull(token);
		checkNotNull(email);
		Collection<User> users = find(token, "email:" + email);
		return users;
	}

	public User findUserByEmail(String token, String email) throws ServiceException {
		checkNotNull(token);
		checkNotNull(email);
		Collection<User> users = find(token, "email:" + email);
		if (users.size() > 0) {
			return Iterables.get(users, 0);
		}
		return null;
	}

	public Collection<User> findUsersByName(String token, String name) throws ServiceException {
		checkNotNull(token);
		checkNotNull(name);
		Collection<User> users = find(token, "name:" + name);
		return users;
	}

	public User findUserByName(String token, String name) throws ServiceException {
		checkNotNull(token);
		checkNotNull(name);
		Collection<User> users = find(token, "name:" + name);
		if (users.size() > 0) {
			return Iterables.get(users, 0);
		}
		return null;
	}

	private Collection<User> find(String token, String criteria) {
		Cookie cookie = newUserCookie(token);
		Response response = getWebTarget().path(USERS_PATH).path(SEARCH_PATH)
				.path(criteria).request(MediaType.APPLICATION_JSON)
				.cookie(cookie).get();
		if (response.getStatus() == Status.OK.getStatusCode()) {
			SearchResult<User> users = response
					.readEntity(new GenericType<SearchResult<User>>() {
					});
			log.debug(String.format("TotalHits: %s", users.getTotalHits()));
			if (users.getTotalHits() > 0) {
				return users.getItems();
			}
		} else {
			log.warn(String.format("users/search returned reponse status: %s", response.getStatus()));
		}
		return newHashSet();
	}
}
