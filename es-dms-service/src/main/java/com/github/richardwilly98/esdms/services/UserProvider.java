package com.github.richardwilly98.esdms.services;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.ByteSource;
import org.elasticsearch.client.Client;

import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.BootstrapService;
import com.github.richardwilly98.esdms.services.HashService;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.inject.Inject;

public class UserProvider extends ProviderBase<User> implements UserService {

	private final static String type = "user";
	private final HashService hashService;
	private final RoleService roleService;

	@Inject
	UserProvider(Client client, BootstrapService bootstrapService,
			HashService hashService, RoleService roleService)
			throws ServiceException {
		super(client, bootstrapService, null, UserProvider.type, User.class);
		this.hashService = hashService;
		this.roleService = roleService;
	}

	@Override
	protected void loadInitialData() throws ServiceException {
		User user = new UserImpl.Builder()
				.hash(computeBase64Hash(DEFAULT_ADMIN_PASSWORD))
				.id(DEFAULT_ADMIN_LOGIN).name(DEFAULT_ADMIN_LOGIN)
				.description(DEFAULT_ADMIN_DESCRIPTION)
				.email(DEFAULT_ADMIN_LOGIN).build();
		Role role = roleService.get(RoleService.ADMINISTRATOR_ROLE_ID);
		user.addRole(role);
		super.create(user);
	}

	@Override
	protected String getMapping() {
		return null;
	}

	private String computeBase64Hash(String password) {
		return hashService.toBase64(ByteSource.Util.bytes(
				password.toCharArray()).getBytes());
	}

	@RequiresPermissions(CREATE_PERMISSION)
	@Override
	public User create(User user) throws ServiceException {
		try {
			if (user.getId() == null) {
				user.setId(generateUniqueId(user));
			}
			if (user.getPassword() != null) {
				String encodedHash = computeBase64Hash(user.getPassword());
				if (log.isTraceEnabled()) {
					log.trace(String.format(
							"From service - hash: %s for login %s",
							encodedHash, user.getLogin()));
				}
				user.setHash(encodedHash);
				user.setPassword(null);

			}

			if (user.getRoles().isEmpty()) {
				Role role = roleService.get(RoleService.WRITER_ROLE_ID);
				if (role != null) {
					user.addRole(role);
				} else {
					throw new ServiceException(
							"Could not find default role "
									+ RoleService.WRITER_ROLE_ID);
				}
			}

			User newUser = super.create(user);
			return newUser;
		} catch (Throwable t) {
			log.error("create failed", t);
			throw new ServiceException(t.getLocalizedMessage());
		}
	}

	@Override
	public User update(User item) throws ServiceException {
		if (item.getPassword() != null) {
			String encodedHash = computeBase64Hash(item.getPassword());
			if (log.isTraceEnabled()) {
				log.trace(String.format("From service - hash: %s for login %s",
						encodedHash, item.getLogin()));
			}
			item.setHash(encodedHash);
			item.setPassword(null);
		}
		return super.update(item);
	}

}
