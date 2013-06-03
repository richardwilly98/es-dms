package com.github.richardwilly98.esdms.inject;

import com.github.richardwilly98.esdms.services.AuthenticationProvider;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.DocumentProvider;
import com.github.richardwilly98.esdms.services.DocumentService;
import com.github.richardwilly98.esdms.services.HashService;
import com.github.richardwilly98.esdms.services.PermissionProvider;
import com.github.richardwilly98.esdms.services.PermissionService;
import com.github.richardwilly98.esdms.services.RoleProvider;
import com.github.richardwilly98.esdms.services.RoleService;
import com.github.richardwilly98.esdms.services.SHA512HashProvider;
import com.github.richardwilly98.esdms.services.UserProvider;
import com.github.richardwilly98.esdms.services.UserService;
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
