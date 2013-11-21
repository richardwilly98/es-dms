package com.github.richardwilly98.esdms.services;

/*
 * #%L
 * es-dms-service
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.SessionImpl;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.search.api.SearchResult;
import com.github.richardwilly98.esdms.api.Session;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;

@Singleton
public class AuthenticationProvider implements AuthenticationService {

    public static final String ES_DMS_LOGIN_ATTRIBUTE = "ES_DMS_LOGIN";
    public static final String ES_DMS_ID_ATTRIBUTE = "ES_DMS_ID";

    private static Logger log = Logger.getLogger(AuthenticationProvider.class);

    private final static String index = "system";
    private final static String type = "session";

    private final static ObjectMapper mapper = new ObjectMapper();
    private final Client client;
    private final org.apache.shiro.mgt.SecurityManager securityManager;
    private final UserService userService;

    @Inject
    AuthenticationProvider(final Client client, final org.apache.shiro.mgt.SecurityManager securityManager, final UserService userService)
            throws ServiceException {
        this.client = client;
        this.securityManager = securityManager;
        this.userService = userService;
        SecurityUtils.setSecurityManager(securityManager);
        createIndex();
        refreshIndex();
    }

    @Override
    public SessionImpl get(String id) throws ServiceException {
        try {
            // if (log.isTraceEnabled()) {
            // log.trace(String.format("get - %s", id));
            // }
            GetResponse response = client.prepareGet(index, type, id).execute().actionGet();
            if (!response.isExists()) {
                log.info(String.format("Cannot find item %s", id));
                return null;
            }
            String json = response.getSourceAsString();
            SessionImpl session = mapper.readValue(json, SessionImpl.class);
//            validateSession(session);
            return session;
        } catch (Throwable t) {
            log.error("get failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    @Override
    public boolean exists(String id) throws ServiceException {
        try {
            if (log.isTraceEnabled()) {
                log.trace(String.format("exists - %s", id));
            }
            return client.prepareGet(index, type, id).setFields(new String[0]).execute().actionGet().isExists();
        } catch (Throwable t) {
            log.error("exists failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    @Override
    public String login(Credential credential) throws ServiceException {
        String login = credential.getUsername();
        char[] password = credential.getPassword();
        boolean rememberMe = credential.isRememberMe();
        try {
            if (log.isTraceEnabled()) {
                log.trace(String.format("login - %s", credential));
            }
            UsernamePasswordToken token = new UsernamePasswordToken(login, password, rememberMe);
            AuthenticationInfo info = securityManager.authenticate(token);
            if (log.isTraceEnabled()) {
                if (info instanceof SimpleAuthenticationInfo) {
                    PrincipalCollection principals = ((SimpleAuthenticationInfo) info).getPrincipals();
                    for (Object principal : principals.asList()) {
                        log.trace("Principal: " + principal);
                    }
                }
            }
            token.clear();
            // Create subject for the current principal
            Subject subject = new Subject.Builder().principals(info.getPrincipals()).buildSubject();
            // log.trace("subject.getPrincipal(): " + subject.getPrincipal());
            // Create session
            org.apache.shiro.session.Session session = subject.getSession(true);
            if (session == null) {
                throw new ServiceException(String.format("Unable to create session for ", login));
            }
            session.setAttribute(ES_DMS_LOGIN_ATTRIBUTE, login);
            session.setAttribute(ES_DMS_ID_ATTRIBUTE, ((User)subject.getPrincipal()).getId());
            ThreadContext.bind(subject);
            // if (log.isTraceEnabled()) {
            // Subject currentUser = SecurityUtils.getSubject();
            // log.trace("currentUser.getPrincipal(): " +
            // currentUser.getPrincipal());
            // }
            return session.getId().toString();
        } catch (AuthenticationException aEx) {
            String message = String.format("Authentication failed for %s", login);
            log.error(message, aEx);
            throw new ServiceException(message);
        }
    }

    @Override
    public void logout(String token) throws ServiceException {
        if (log.isTraceEnabled()) {
            log.trace(String.format("logout - %s", token));
        }
        Subject subject = getSubjectBySessionId(token);
        if (subject != null) {
            subject.logout();
        }
    }

    private void validateSession(SessionImpl session) throws ServiceException {
        long timeout = session.getTimeout();
        if (timeout != 0) {
            DateTime lastAccessTime = new DateTime(session.getLastAccessTime().getTime());
            DateTime timeoutTime = lastAccessTime.plus(timeout);
            if (timeoutTime.isBeforeNow()) {
                throw new ServiceException(String.format("Session %s has timed-out", session.getId()));
            }
        }
    }

    // gets or creates a session with the given sessionId
    private Subject getSubjectBySessionId(String sessionId) throws ServiceException {
        Subject subject = null;
        try {
            subject = new Subject.Builder(securityManager).sessionId(sessionId).buildSubject();
            subject.getSession(true);
        } finally {
        }
        return subject;
    }

    private Subject getSubjectByPrincipal(PrincipalCollection principals) {
        Subject currentUser = new Subject.Builder().principals(principals).buildSubject();
        return currentUser;
    }

    private PrincipalCollection getPrincipals(String token) throws ServiceException {
        User user = null;
        Session session = get(token);
        if (session != null) {
            String login = session.getUserId();
            user = userService.get(login);
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, "", "");
        return info.getPrincipals();
    }

    private Subject getSubjectFromSessionId(String token) throws ServiceException {
        Subject subject = getSubjectByPrincipal(getPrincipals(token));
        return subject;
    }

    @Override
    public SessionImpl validate(String token) throws ServiceException {
        SessionImpl session = get(token);
        if (session != null) {
            validateSession(session);
            session.setLastAccessTime(new Date());
            return update(session);
        }
        throw new ServiceException(String.format("Cannot validate session %s", token));
    }

    @Override
    public SessionImpl create(SessionImpl item) throws ServiceException {
        try {
            if (log.isTraceEnabled()) {
                log.trace(String.format("create - %s", item));
            }
            if (item.getId() == null) {
                item.setId(generateUniqueId());
            }
            String json;
            json = mapper.writeValueAsString(item);
            IndexResponse response = client.prepareIndex(index, type).setId(item.getId()).setSource(json).execute().actionGet();
            log.trace(String.format("Index: %s - Type: %s - Id: %s", response.getIndex(), response.getType(), response.getId()));
            refreshIndex();
            SessionImpl newSession = get(response.getId());
            return newSession;
        } catch (Throwable t) {
            log.error("create failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    @Override
    public void delete(SessionImpl session) throws ServiceException {
        try {
            if (log.isTraceEnabled()) {
                log.trace(String.format("delete - %s", session));
            }
            if (session == null) {
                throw new IllegalArgumentException("session is null");
            }
            client.prepareDelete(index, type, session.getId()).execute().actionGet();
        } catch (Throwable t) {
            log.error("delete failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    @Override
    public SessionImpl update(SessionImpl session) throws ServiceException {
        try {
            String json = mapper.writeValueAsString(session);
            client.prepareDelete(index, type, session.getId());
            UpdateResponse response = client.prepareUpdate(index, type, session.getId()).setDoc(json).setRetryOnConflict(5).execute()
                    .actionGet();
            // refreshIndex();
            SessionImpl updatedSession = get(response.getId());
            return updatedSession;
        } catch (Throwable t) {
            log.error("update failed", t);
            throw new ServiceException(t.getLocalizedMessage());
        }
    }

    private void createIndex() {
        if (!client.admin().indices().prepareExists(index).execute().actionGet().isExists()) {
            client.admin().indices().prepareCreate(index).execute().actionGet();
        }
    }

    private void refreshIndex() {
        client.admin().indices().refresh(new RefreshRequest(index)).actionGet();
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean hasRole(String token, String role) throws ServiceException {
        Subject subject = getSubjectFromSessionId(token);
        if (subject != null) {
            return subject.hasRole(role);
        }
        return false;
    }

    @Override
    public boolean hasPermission(String token, String permission) throws ServiceException {
        if (log.isTraceEnabled()) {
            log.trace(String.format("hasPermission - %s - %s", token, permission));
        }
        Subject subject = getSubjectFromSessionId(token);
        if (subject != null) {
            try {
                subject.checkPermission(permission);
                return true;
            } catch (AuthorizationException aEx) {
                log.warn("checkPermission failed " + aEx.getLocalizedMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public Set<SessionImpl> getItems(String name) throws ServiceException {
        try {
            if (log.isTraceEnabled()) {
                log.trace(String.format("getItems - %s", name));
            }
            Set<SessionImpl> items = new HashSet<SessionImpl>();
            SearchResponse searchResponse = client.prepareSearch(index).setTypes(type).setQuery(QueryBuilders.queryString(name)).execute()
                    .actionGet();
            log.debug("totalHits: " + searchResponse.getHits().totalHits());
            for (SearchHit hit : searchResponse.getHits().hits()) {
                String json = hit.getSourceAsString();
                try {
                    SessionImpl item = mapper.readValue(json, SessionImpl.class);
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
    public SearchResult<SessionImpl> search(String criteria, int first, int pageSize) throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean disabled(SessionImpl item) throws ServiceException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void disable(SessionImpl item, boolean b) throws ServiceException {
        // TODO Auto-generated method stub

    }

    @PostConstruct
    public void start() throws ServiceException {
        log.info("start " + this.getClass().getName());
    }

    @PreDestroy
    public void stop() throws ServiceException {
        log.info("stop " + this.getClass().getName());
    }
}
