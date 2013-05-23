package test.github.richardwilly98.inject;

import org.elasticsearch.client.Client;

import com.google.inject.AbstractModule;

public class TestEsClientModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Client.class).toProvider(LocalClientProvider.class).asEagerSingleton();
	}

}
