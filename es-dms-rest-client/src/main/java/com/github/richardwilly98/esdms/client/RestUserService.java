package com.github.richardwilly98.esdms.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.google.common.collect.Iterables;

public class RestUserService extends RestClientBase {

    public static final String SEARCH_PATH = "search";
    public static final String SEARCH_FIRST_PARAMETER = "fi";
    public static final String SEARCH_PAGE_SIZE_PARAMETER = "ps";
    public static final String USERS_PATH = "users";
    public static final String LOGIN_PARAM = "login";

    public RestUserService(String url) {
        super(url);
    }

    public Collection<User> findUsersById(String token, String id, int... params) throws ServiceException {
        checkNotNull(token);
        checkNotNull(id);
        Collection<User> users = find(token, "id:" + id, params);
        return users;
    }

    public User findUserById(String token, String id) throws ServiceException {
        checkNotNull(token);
        checkNotNull(id);
        Cookie cookie = newUserCookie(token);
        Response response = target().path(USERS_PATH).path(id).request(MediaType.APPLICATION_JSON).cookie(cookie).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(User.class);
        }
        return null;
    }

    public Collection<User> findUsersByEmail(String token, String email, int... params) throws ServiceException {
        checkNotNull(token);
        checkNotNull(email);
        Collection<User> users = find(token, "email:" + email, params);
        return users;
    }

    public User findUserByEmail(String token, String email) throws ServiceException {
        checkNotNull(token);
        checkNotNull(email);
        Collection<User> users = find(token, "email:" + email, 0, 1);
        if (users.size() > 0) {
            return Iterables.get(users, 0);
        }
        return null;
    }

    public Collection<User> findUsersByName(String token, String name, int... params) throws ServiceException {
        checkNotNull(token);
        checkNotNull(name);
        Collection<User> users = find(token, "name:" + name, params);
        return users;
    }

    public User findUserByName(String token, String name) throws ServiceException {
        checkNotNull(token);
        checkNotNull(name);
        Collection<User> users = find(token, "name:" + name, 0, 1);
        if (users.size() > 0) {
            return Iterables.get(users, 0);
        }
        return null;
    }

    public User findUserByLogin(String token, String login) throws ServiceException {
        checkNotNull(token);
        checkNotNull(login);
        Cookie cookie = newUserCookie(token);
        Response response = target().path(RestUserService.USERS_PATH).queryParam(RestUserService.LOGIN_PARAM, login)
                .request(MediaType.APPLICATION_JSON).cookie(cookie).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(User.class);
        }
        return null;
    }

    public User create(String token, User user) throws ServiceException {
        checkNotNull(token);
        checkNotNull(user);
        Cookie cookie = newUserCookie(token);
        Response response = target().path(RestUserService.USERS_PATH).request(MediaType.APPLICATION_JSON).cookie(cookie)
                .post(Entity.json(user));
        log.debug(String.format("status: %s", response.getStatus()));
        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            throw new ServiceException(String.format("Fail to create user. Response stauts: %s", response.getStatus()));
        }
        URI uri = response.getLocation();
        log.debug(String.format("getItem - %s", uri));
        response = restClient.target(uri).request().cookie(cookie).accept(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(User.class);
        }
        return null;
    }

    private Collection<User> find(String token, String criteria, int... params) {
        Cookie cookie = newUserCookie(token);
        int first = 0;
        int pageSize = 20;
        if (params.length > 0) {
            first = params[0];
        }
        if (params.length > 1) {
            pageSize = params[1];
        }

        Response response = target().path(USERS_PATH).path(SEARCH_PATH).path(criteria).queryParam(SEARCH_FIRST_PARAMETER, first)
                .queryParam(SEARCH_PAGE_SIZE_PARAMETER, pageSize).request(MediaType.APPLICATION_JSON).cookie(cookie).get();
        if (response.getStatus() == Status.OK.getStatusCode()) {
            SearchResult<User> users = response.readEntity(new GenericType<SearchResult<User>>() {
            });
            log.trace(String.format("TotalHits: %s", users.getTotalHits()));
            if (users.getTotalHits() > 0) {
                return users.getItems();
            }
        } else {
            log.warn(String.format("users/search returned reponse status: %s", response.getStatus()));
        }
        return newHashSet();
    }
}
