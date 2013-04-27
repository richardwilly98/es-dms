package test.github.richardwilly98.services;

import org.elasticsearch.client.Client;

import com.github.richardwilly98.api.services.UserService;
import com.github.richardwilly98.services.UserProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ProviderModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Client.class).toProvider(LocalClientProvider.class).in(Scopes.SINGLETON);
		bind(UserService.class).to(UserProvider.class);
	}

}
