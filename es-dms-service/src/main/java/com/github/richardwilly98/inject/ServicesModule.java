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

public class ServicesModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(HashService.class).to(SHA512HashProvider.class)
				.asEagerSingleton();
		bind(AuthenticationService.class).to(AuthenticationProvider.class).asEagerSingleton();
		bind(PermissionService.class).to(PermissionProvider.class).asEagerSingleton();
		bind(RoleService.class).to(RoleProvider.class).asEagerSingleton();
		bind(UserService.class).to(UserProvider.class).asEagerSingleton();
		bind(DocumentService.class).to(DocumentProvider.class).asEagerSingleton();
	}

}
