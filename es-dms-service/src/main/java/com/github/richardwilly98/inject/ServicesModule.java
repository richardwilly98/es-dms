package com.github.richardwilly98.inject;

import com.github.richardwilly98.api.services.AuthenticationService;
import com.github.richardwilly98.api.services.DocumentService;
import com.github.richardwilly98.api.services.HashService;
import com.github.richardwilly98.api.services.PermissionService;
import com.github.richardwilly98.api.services.RoleService;
import com.github.richardwilly98.api.services.UserService;
import com.github.richardwilly98.services.AuthenticationProvider;
import com.github.richardwilly98.services.DocumentProvider;
import com.github.richardwilly98.services.PermissionProvider;
import com.github.richardwilly98.services.RoleProvider;
import com.github.richardwilly98.services.SHA512HashProvider;
import com.github.richardwilly98.services.UserProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ServicesModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(HashService.class).to(SHA512HashProvider.class)
				.in(Scopes.SINGLETON);
		bind(AuthenticationService.class).to(AuthenticationProvider.class).in(
				Scopes.SINGLETON);
		bind(UserService.class).to(UserProvider.class).in(Scopes.SINGLETON);
		bind(DocumentService.class).to(DocumentProvider.class).in(
				Scopes.SINGLETON);
		bind(PermissionService.class).to(PermissionProvider.class).in(
				Scopes.SINGLETON);
		bind(RoleService.class).to(RoleProvider.class).in(Scopes.SINGLETON);

	}

}
