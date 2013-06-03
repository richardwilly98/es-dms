package com.github.richardwilly98.esdms.inject;

import org.elasticsearch.client.Client;

import com.github.richardwilly98.esdms.es.ClientProvider;
import com.google.inject.AbstractModule;

public class EsClientModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Client.class).toProvider(ClientProvider.class).asEagerSingleton();
	}

}
