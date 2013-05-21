package com.github.richardwilly98.services;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.ISession;
import com.github.richardwilly98.api.Session;
import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.AuthenticationService;
import com.github.richardwilly98.api.services.UserService;
import com.google.inject.Inject;

public class AuthenticationProvider implements AuthenticationService {

	public static final String ES_DMS_LOGIN_ATTRIBUTE = "ES_DMS_LOGIN";

	private static Logger log = Logger.getLogger(AuthenticationProvider.class);

	private final static String index = "system";
	private final static String type = "session";
	
	private final static ObjectMapper mapper = new ObjectMapper();
	private final Client client;
	private final org.apache.shiro.mgt.SecurityManager securityManager;
	private final UserService userService;

	@Inject
	AuthenticationProvider(final Client client,
			final org.apache.shiro.mgt.SecurityManager securityManager,
			final UserService userService) throws ServiceException {
		this.client = client;
		this.securityManager = securityManager;
		this.userService = userService;
		SecurityUtils.setSecurityManager(securityManager);
		createIndex();
		refreshIndex();
	}

	@Override
	public ISession get(String id) throws ServiceException {
		try {
			GetResponse response = client.prepareGet(index, type, id).execute()
					.actionGet();
			if (!response.isExists()) {
				return null;
			}
			String json = response.getSourceAsString();
			ISession permission = mapper.readValue(json, Session.class);
			return permission;
		} catch (Throwable t) {
			log.error("get failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public List<ISession> getList(String name) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<ISession> getItems(String name) throws ServiceException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Set<ISession> getItems() throws ServiceException {
		throw new UnsupportedOperationException();
	}


	@Override
	public List<ISession> search(String criteria) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String login(Credential credential) throws ServiceException {
		String login = credential.getUsername();
		char[] password = credential.getPassword().toCharArray();
		boolean rememberMe = credential.isRememberMe();
		try {
			UsernamePasswordToken token = new UsernamePasswordToken(login,
					password, rememberMe);
			AuthenticationInfo info = securityManager.authenticate(token);
			if (info instanceof SimpleAuthenticationInfo) {
				PrincipalCollection principals = ((SimpleAuthenticationInfo) info)
						.getPrincipals();

				for (Object principal : principals.asList()) {
					log.debug("Principal: " + principal);
				}
			}
			token.clear();
			// Create subject for the current principal
			Subject subject = new Subject.Builder().principals(
					info.getPrincipals()).buildSubject();
			log.trace("subject.getPrincipal(): " + subject.getPrincipal());
			// Create session
			org.apache.shiro.session.Session session = subject
					.getSession(true);
			if (session == null) {
				throw new ServiceException(String.format(
						"Unable to create session for ", login));
			}
			session.setAttribute(ES_DMS_LOGIN_ATTRIBUTE, login);
            ThreadContext.bind(subject);
            if (log.isTraceEnabled()) {
                Subject currentUser = SecurityUtils.getSubject();
                log.trace("currentUser.getPrincipal(): " + currentUser.getPrincipal());
            }
			return session.getId().toString();
		} catch (AuthenticationException aEx) {
			String message = String.format("Authentication failed for %s",
					login);
			log.error(message, aEx);
			throw new ServiceException(message);
		}
	}

	@Override
	public void logout(String token) throws ServiceException {
		Subject subject = getSubjectBySessionId(token);
		if (subject != null) {
			subject.logout();
		}
	}

	// gets or creates a session with the given sessionId
	private Subject getSubjectBySessionId(String sessionId)
			throws ServiceException {
		Subject subject = null;
		try {
			subject = new Subject.Builder(securityManager).sessionId(sessionId)
					.buildSubject();
			subject.getSession(true);
		} finally {
		}
		return subject;
	}

	private Subject getSubjectByPrincipal(PrincipalCollection principals) {
		Subject currentUser = new Subject.Builder().principals(principals)
				.buildSubject();
		return currentUser;
	}

	private PrincipalCollection getPrincipals(String token)
			throws ServiceException {
		User user = null;
		ISession session = get(token);
		if (session != null) {
			String login = session.getUserId();
			user = userService.get(login);
		}
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, "", "");
		return info.getPrincipals();
	}

	private Subject getSubjectFromSessionId(String token)
			throws ServiceException {
		Subject subject = getSubjectByPrincipal(getPrincipals(token));
		return subject;
	}

	@Override
	public void validate(String token) throws ServiceException {
		ISession session = get(token);
		if (session != null) {
			session.setLastAccessTime(new Date());
			update(session);
		}
	}

	@Override
	public ISession create(ISession item) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("create - %s", item));
			}
			if (item.getId() == null) {
				item.setId(generateUniqueId());
			}
			String json;
			json = mapper.writeValueAsString(item);
			IndexResponse response = client.prepareIndex(index, type)
					.setId(item.getId()).setSource(json).execute().actionGet();
			log.trace(String.format("Index: %s - Type: %s - Id: %s",
					response.getIndex(), response.getType(), response.getId()));
			refreshIndex();
			ISession newSession = get(response.getId());
			return newSession;
		} catch (Throwable t) {
			log.error("create failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public void delete(ISession session) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("delete - %s", session));
			}
			if (session == null) {
				throw new IllegalArgumentException("session is null");
			}
			client.prepareDelete(index, type, session.getId()).execute()
					.actionGet();
		} catch (Throwable t) {
			log.error("delete failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public ISession update(ISession session) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("update - %s", session));
			}
			String json;
			json = mapper.writeValueAsString(session);
			client.prepareDelete(index, type, session.getId());
			UpdateResponse response = client
					.prepareUpdate(index, type, session.getId()).setDoc(json).setRetryOnConflict(5)
					.execute().actionGet();
//			refreshIndex();
			ISession updatedSession = get(response.getId());
			return updatedSession;
		} catch (Throwable t) {
			log.error("update failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public boolean disabled(ISession session) throws ServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void disable(ISession session, boolean b) throws ServiceException {
		// TODO Auto-generated method stub

	}

	private void createIndex() {
		if (!client.admin().indices().prepareExists(index).execute()
				.actionGet().isExists()) {
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
	public boolean hasPermission(String token, String permission)
			throws ServiceException {
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
}
