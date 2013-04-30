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

	// protected SimpleAccount getAccount(String username) {
	// //just create a dummy. A real app would construct one based on EIS
	// access.
	// SimpleAccount account = new SimpleAccount(username,
	// "sha256EncodedPasswordFromDatabase", getName());
	// //simulate some roles and permissions:
	// account.addRole("user");
	// account.addRole("admin");
	// //most applications would assign permissions to Roles instead of users
	// directly because this is much more
	// //flexible (it is easier to configure roles and then change role-to-user
	// assignments than it is to maintain
	// // permissions for each user).
	// // But these next lines assign permissions directly to this trivial
	// account object just for simulation's sake:
	// account.addStringPermission("blogEntry:edit"); //this user is allowed to
	// 'edit' _any_ blogEntry
	// //fine-grained instance level permission:
	// account.addStringPermission("printer:print:laserjet2000"); //allowed to
	// 'print' to the 'printer' identified
	// //by the id 'laserjet2000'
	//
	// return account;
	// }

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		log.debug("*** doGetAuthorizationInfo ***");
		Collection<User> principalsList = principals.byType(User.class);
		if (principals.isEmpty()) {
			throw new AuthorizationException("Empty principals list!");
		}

		for (User userPrincipal : principalsList) {
			log.debug(userPrincipal);
		}

		Set<String> roles = new HashSet<String>();
		Set<String> permissions = new HashSet<String>();
		roles.add("reader");
		permissions.add("document:create");
		permissions.add("document:delete");
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setRoles(roles);
		info.setStringPermissions(permissions);
		return info;
		// //get the principal this realm cares about:
		// String username = (String) getAvailablePrincipal(principals);
		//
		// //call the underlying EIS for the account data:
		// return getAccount(username);
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
			SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
					upToken.getUsername(), upToken.getPassword(), getName());
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
