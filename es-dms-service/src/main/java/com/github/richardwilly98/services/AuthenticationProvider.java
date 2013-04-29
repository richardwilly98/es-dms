package com.github.richardwilly98.services;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.util.ByteSource;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.Session;
import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.AuthenticationService;
import com.github.richardwilly98.api.services.HashService;
import com.google.inject.Inject;

public class AuthenticationProvider extends ProviderBase<Session> implements
		AuthenticationService {

	private static boolean accountCreated = false;

	private final static String index = "system";
	private final static String type = "session";
	private final UserProvider userProvider;
	private final HashService hashService;
	
	@Inject
	AuthenticationProvider(final Client client, final HashService service) {
		super(client, AuthenticationProvider.index, AuthenticationProvider.type, Session.class);
		this.hashService = service;
		userProvider = new UserProvider(client, service);
	}

	@Override
	protected String generateUniqueId(Session session) {
		return super.generateUniqueId(session);
	}

	protected void createIndex() {
		if (!client.admin().indices().prepareExists(index).execute()
				.actionGet().exists()) {
			client.admin().indices().prepareCreate(index).execute().actionGet();
		}
	}

	@Override
	public Session get(String id) throws ServiceException {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("get - %s", id));
			}
			GetResponse response = client.prepareGet(index, type, id).execute()
					.actionGet();
			if (!response.exists()) {
				return null;
			}
			String json = response.getSourceAsString();
			Session permission = mapper.readValue(json, Session.class);
			return permission;
		} catch (Throwable t) {
			log.error("getPermission failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public List<Session> getList(String name) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Session> getItems(String name) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Session> search(String criteria) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String login(Credential credential) throws ServiceException {
		String login = credential.getLogin();
		char[] password = credential.getPassword().toCharArray();
		User user = findPrincipal(login);
		if (user == null) {
			user = shouldCreateAccount(login, password);
		}
		if (user == null) {
			throw new AuthenticationException("Login name [" + login + "] not found!");
		}

		String hash = computeBase64Hash(password);
		if (log.isTraceEnabled()) {
			log.trace("hash: " + hash);
		}
		if (hash.equals(user.getHash())) {
			Session session = new Session();
			session.setUserId(login);
			session.setCreateTime(new Date());
			session.setLastAccessedTime(new Date());
			session.setActive(true);
			session = create(session);
			return session.getId();
		} else {
			throw new AuthenticationException("Password not matching for login name [" + login + ".");
		}
	}

	@Override
	public void logout(String token) throws ServiceException {
		Session session = get(token);
		if (session != null) {
			delete(session);
		}
	}

	private User findPrincipal(String name) {
		try {
			List<User> users = userProvider.getList(name);
			log.debug("users size: " + users.size());
			for(User user : users) {
				if (user.getId().equals(name)) {
					return user;
				}
			}
		} catch (ServiceException ex) {
			log.error("findPrincipal failed", ex);
		}
		return null;
	}

	private String computeBase64Hash(char[] password) {
		return hashService.toBase64(ByteSource.Util.bytes(password).getBytes());
	}
	
	private User shouldCreateAccount(String username, char[] password) {
		if (accountCreated) {
			return null;
		}
		try {
			List<User> users = userProvider.getList("*");
			accountCreated = true;
			if (users.size() == 0) {
				User user = new User();
				user.setName(username);
				user.setDescription("System administrator");
				user.setHash(computeBase64Hash(password));
				user.setEmail("admin@admin");
				user = userProvider.create(user);
				return user;
			}
		} catch (ServiceException ex) {
			log.error("shouldCreateAccount failed", ex);
		}
		return null;
	}
}
