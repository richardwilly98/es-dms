package test.github.richardwilly98.inject;

import org.elasticsearch.client.Client;


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
import com.github.richardwilly98.services.SHA512HashService;
import com.github.richardwilly98.services.UserProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ProviderModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Client.class).toProvider(LocalClientProvider.class).in(Scopes.SINGLETON);
		bind(HashService.class).to(SHA512HashService.class).in(Scopes.SINGLETON);
		bind(AuthenticationService.class).to(AuthenticationProvider.class);
		bind(UserService.class).to(UserProvider.class);
		bind(DocumentService.class).to(DocumentProvider.class);
		bind(PermissionService.class).to(PermissionProvider.class);
		bind(RoleService.class).to(RoleProvider.class);
	}

}
