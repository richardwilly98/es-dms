package com.github.richardwilly98.shiro;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import com.github.richardwilly98.api.Permission;
import com.github.richardwilly98.api.Role;
import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.HashService;
import com.github.richardwilly98.services.UserProvider;
import com.google.inject.Inject;

public class EsRealm extends AuthorizingRealm {

	private final HashService hashService;
	private final UserProvider provider;
	private static boolean accountCreated = false;

	private static Logger log = Logger.getLogger(EsRealm.class);

	@Inject
	public EsRealm(final UserProvider provider, final HashService hashService) {
		this.provider = provider;
		this.hashService = hashService;
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		log.debug("*** supports ***");
		return (token instanceof UsernamePasswordToken);
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		log.debug("*** doGetAuthorizationInfo ***");
		Collection<User> principalList = principals.byType(User.class);
		if (principals.isEmpty()) {
			throw new AuthorizationException("Empty principal list!");
		}

		User principal = principalList.iterator().next();
		Set<String> roles = new HashSet<String>();
		Set<String> permissions = new HashSet<String>();
		for(Role role : principal.getRoles()) {
			roles.add(role.getId());
			for(Permission permission : role.getPermissions()) {
				permissions.add(permission.getId());
			}
		}
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setRoles(roles);
		info.setStringPermissions(permissions);
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		log.debug("*** doGetAuthenticationInfo ***");
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		if (log.isTraceEnabled()) {
			log.trace(String.format("authenticate - %s", upToken.getUsername()));
		}

		User user = getPrincipal(upToken.getUsername());
		if (user == null) {
			user = shouldCreateAccount(upToken.getUsername(),
					upToken.getPassword());
		}
		if (user == null) {
			throw new AuthenticationException(String.format(
					"Login name [%s] not found!", upToken.getUsername()));
		}

		String hash = computeBase64Hash(upToken.getPassword());
		if (log.isTraceEnabled()) {
			log.trace("hash: " + hash);
		}
		if (hash.equals(user.getHash())) {
//			SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
//					upToken.getUsername(), upToken.getPassword(), getName());
			SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
					user, upToken.getPassword(), getName());
			return info;
		} else {
			throw new AuthenticationException(String.format(
					"Password not matching for login name [%s]",
					upToken.getUsername()));
		}
	}

	private String computeBase64Hash(char[] password) {
		return hashService.toBase64(ByteSource.Util.bytes(password).getBytes());
	}

	private User shouldCreateAccount(String username, char[] password) {
		if (accountCreated) {
			return null;
		}
		try {
			List<User> users = provider.getList("*");
			accountCreated = true;
			if (users.size() == 0) {
				User user = new User();
				user.setName(username);
				user.setDescription("System administrator");
				user.setHash(computeBase64Hash(password));
				user.setEmail("admin@admin");
				user = provider.create(user);
				return user;
			}
		} catch (ServiceException ex) {
			log.error("shouldCreateAccount failed", ex);
		}
		return null;
	}

	private User getPrincipal(String login) {
		try {
			return provider.get(login);
		} catch (ServiceException ex) {
			log.error("getPrincipal failed", ex);
		}
		return null;
	}

}
