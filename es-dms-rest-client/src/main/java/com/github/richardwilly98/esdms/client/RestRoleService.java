package com.github.richardwilly98.esdms.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.google.common.collect.Iterables;

public class RestRoleService extends RestClientBase {

	public static final String SEARCH_PATH = "search";
	public static final String ROLES_PATH = "roles";

	public RestRoleService(String url) {
		super(url);
	}

	public Role findRoleById(String token, String id) throws ServiceException {
		checkNotNull(token);
		checkNotNull(id);
		Cookie cookie = newUserCookie(token);
		Response response = getWebTarget().path(ROLES_PATH)
				.path(id).request(MediaType.APPLICATION_JSON)
				.cookie(cookie).get();
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return response.readEntity(Role.class);
		}
		return null;
	}

	public Collection<Role> findRolesByType(String token, RoleType type)
			throws ServiceException {
		checkNotNull(token);
		checkNotNull(type);
//		Collection<Role> roles = find(token, "id:" + id);
		Cookie cookie = newUserCookie(token);
		Response response = getWebTarget().path(ROLES_PATH)
				.queryParam("type", type.getType()).request(MediaType.APPLICATION_JSON)
				.cookie(cookie).get();
		if (response.getStatus() == Status.OK.getStatusCode()) {
			SearchResult<Role> roles = response
					.readEntity(new GenericType<SearchResult<Role>>() {
					});
			log.debug(String.format("TotalHits: %s", roles.getTotalHits()));
			if (roles.getTotalHits() > 0) {
				return roles.getItems();
			}
		} else {
			log.warn(String.format("users/search returned reponse status: %s",
					response.getStatus()));
		}
		return newHashSet();
	}

	public Collection<Role> findRolesById(String token, String id)
			throws ServiceException {
		checkNotNull(token);
		checkNotNull(id);
		Collection<Role> users = find(token, "id:" + id);
		return users;
	}

	public Role findUserById(String token, String id) throws ServiceException {
		checkNotNull(token);
		checkNotNull(id);
		Cookie cookie = newUserCookie(token);
		Response response = getWebTarget().path(ROLES_PATH).path(id)
				.request(MediaType.APPLICATION_JSON).cookie(cookie).get();
		if (response.getStatus() == Status.OK.getStatusCode()) {
			return response.readEntity(Role.class);
		}
		return null;
	}

	public Collection<Role> findUsersByEmail(String token, String email)
			throws ServiceException {
		checkNotNull(token);
		checkNotNull(email);
		Collection<Role> users = find(token, "email:" + email);
		return users;
	}

	public Role findUserByEmail(String token, String email)
			throws ServiceException {
		checkNotNull(token);
		checkNotNull(email);
		Collection<Role> users = find(token, "email:" + email);
		if (users.size() > 0) {
			return Iterables.get(users, 0);
		}
		return null;
	}

	public Collection<Role> findUsersByName(String token, String name)
			throws ServiceException {
		checkNotNull(token);
		checkNotNull(name);
		Collection<Role> users = find(token, "name:" + name);
		return users;
	}

	public Role findUserByName(String token, String name)
			throws ServiceException {
		checkNotNull(token);
		checkNotNull(name);
		Collection<Role> users = find(token, "name:" + name);
		if (users.size() > 0) {
			return Iterables.get(users, 0);
		}
		return null;
	}

	private Collection<Role> find(String token, String criteria) {
		Cookie cookie = newUserCookie(token);
		Response response = getWebTarget().path(ROLES_PATH).path(SEARCH_PATH)
				.path(criteria).request(MediaType.APPLICATION_JSON)
				.cookie(cookie).get();
		if (response.getStatus() == Status.OK.getStatusCode()) {
			SearchResult<Role> users = response
					.readEntity(new GenericType<SearchResult<Role>>() {
					});
			log.debug(String.format("TotalHits: %s", users.getTotalHits()));
			if (users.getTotalHits() > 0) {
				return users.getItems();
			}
		} else {
			log.warn(String.format("users/search returned reponse status: %s",
					response.getStatus()));
		}
		return newHashSet();
	}
}
