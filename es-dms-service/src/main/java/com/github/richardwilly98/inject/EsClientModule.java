package com.github.richardwilly98.inject;

import org.elasticsearch.client.Client;

import com.github.richardwilly98.es.ClientProvider;
import com.google.inject.AbstractModule;

public class EsClientModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Client.class).toProvider(ClientProvider.class).asEagerSingleton();
	}

}
