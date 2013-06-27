package com.github.richardwilly98.esdms.shiro;

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


import java.util.Collection;
import java.util.HashSet;
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

import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.HashService;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.inject.Inject;

public class EsRealm extends AuthorizingRealm {

	private final HashService hashService;
	private final UserService userService;
	private final RoleService roleService;

	private final static Logger log = Logger.getLogger(EsRealm.class);

	@Inject
	public EsRealm(final UserService userService, final HashService hashService, final RoleService roleService) {
		this.userService = userService;
		this.hashService = hashService;
		this.roleService = roleService;
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		log.trace("*** supports ***");
		return (token instanceof UsernamePasswordToken);
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		log.trace("*** doGetAuthorizationInfo ***");
		Collection<User> principalList = principals.byType(User.class);
		if (principals.isEmpty()) {
			throw new AuthorizationException("Empty principal list!");
		}

		User principal = principalList.iterator().next();
		Set<String> roles = new HashSet<String>();
		Set<String> permissions = new HashSet<String>();
		for(Role role : principal.getRoles()) {
			roles.add(role.getId());
			try {
				role = roleService.get(role.getId());
				for(Permission permission : role.getPermissions()) {
					permissions.add(permission.getId());
				}
			} catch (ServiceException ex) {
				log.error(String.format("Cannot get role from id [%s]", role.getId()), ex);
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
		log.trace("*** doGetAuthenticationInfo ***");
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		if (log.isTraceEnabled()) {
			log.trace(String.format("authenticate - %s", upToken.getUsername()));
		}

		User user = getPrincipal(upToken.getUsername());
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

	private User getPrincipal(String login) {
		try {
			return userService.get(login);
		} catch (ServiceException ex) {
			log.error("getPrincipal failed", ex);
		}
		return null;
	}

}
