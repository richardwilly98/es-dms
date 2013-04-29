package com.github.richardwilly98.services;

import org.elasticsearch.client.Client;

import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.HashService;
import com.github.richardwilly98.api.services.UserService;
import com.google.inject.Inject;

public class UserProvider extends ProviderBase<User> implements UserService {

	private final static String index = "users";
	private final static String type = "user";
	private final HashService hashService;

	@Inject
	UserProvider(Client client, HashService service) {
		super(client, UserProvider.index, UserProvider.type, User.class);
		this.hashService = service;
	}

	@Override
	public User create(User user) throws ServiceException {
		try {
			if (user.getId() == null) {
				user.setId(generateUniqueId(user));
			}
			if (user.getPassword() != null) {
				String encodedHash = hashService.toBase64(user.getPassword().getBytes());
				log.trace("From service - hash: " + encodedHash);
				user.setHash(encodedHash);
				user.setPassword(null);
			}
			User newUser = super.create(user);
			return newUser;
		} catch (Throwable t) {
			log.error("create failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

}
