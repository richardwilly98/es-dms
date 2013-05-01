package com.github.richardwilly98.inject;

import org.elasticsearch.client.Client;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class EsClientModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Client.class).toProvider(ClientProvider.class).in(Scopes.SINGLETON);
	}

}
